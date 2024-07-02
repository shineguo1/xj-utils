package org.xj.commons.web3j.event.core;

import org.xj.commons.toolkit.StringUtils;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/13 13:38
 */
@Getter
@Slf4j
public class EventDecoder<T> {

    protected final Class<T> eventClass;
    protected final Event event;
    protected final String eventName;
    /**
     * 事件签名(topic)
     */
    protected final String signature;


    public static <T> EventDecoder<T> create(Class<T> eventClass) {
        EventName nameAnnotation = eventClass.getAnnotation(EventName.class);
        String chainType = nameAnnotation.chainType();
        if (StringUtils.equalsIgnoreCase(chainType, "Starknet") ||
                StringUtils.equalsIgnoreCase(chainType, "STRK")) {
            return new StrkEventDecoder<>(eventClass);
        } else {
            //default: it's an evm-resolver
            return new EventDecoder<>(eventClass);
        }
    }

    protected EventDecoder(Class<T> eventClass) {
        this.eventClass = eventClass;
        this.event = getEvent(eventClass);
        this.eventName = this.event.getName();
        this.signature = getSignature(eventClass, event);
    }

    protected <T> String getSignature(Class<T> eventClass, Event event) {
        EventName nameAnnotation = eventClass.getAnnotation(EventName.class);
        if (Objects.nonNull(nameAnnotation) && StringUtils.isNotEmpty(nameAnnotation.signature())) {
            return nameAnnotation.signature();
        } else if (StringUtils.isNotEmpty(event.getName())) {
            return EventEncoder.encode(event);
        } else {
            throw new RuntimeException(eventClass.getSimpleName() + "无法确定signature");
        }
    }

    public T decode(Log eventLog) {
        try {
            EventValues eventValues = getEventValues(eventLog);
            if (eventValues == null) {
                throw new RuntimeException(String.format("无法解析事件，区块:%s, tx:%s, topic0:%s",
                        eventLog.getBlockNumber(), eventLog.getTransactionHash(), eventLog.getTopics().get(0)));
            }
            // 事件参数
            List<Type> indexedValues = eventValues.getIndexedValues();
            List<Type> nonIndexedValues = eventValues.getNonIndexedValues();

            int indexedIndex = 0;
            int nonIndexedIndex = 0;
            T decodeEvent = eventClass.newInstance();

            Field[] fields = eventClass.getDeclaredFields();
            for (Field field : fields) {
                EventField fieldAnnotation = field.getAnnotation(EventField.class);
                if (Objects.nonNull(fieldAnnotation)) {
                    boolean indexed = fieldAnnotation.indexed();
                    field.setAccessible(true);
                    Type value = indexed ? indexedValues.get(indexedIndex++) : nonIndexedValues.get(nonIndexedIndex++);
                    field.set(decodeEvent, value);
                }
            }
            return decodeEvent;
        } catch (Exception e) {
            log.error(Throwables.getStackTraceAsString(e));
            log.error("事件解码失败，txHash:{}, logIndex:{}", eventLog.getTransactionHash(), eventLog.getLogIndex());
            throw new RuntimeException("事件解码失败");
        }
    }

    /**
     * copy了这个方法 {@link Contract#staticExtractEventParameters}
     * 修改了signature判断。因为在本系统中，存在event只知道topic不知道EventName
     *
     * @param logEvent
     * @return
     */
    protected EventValues getEventValues(Log logEvent) {
        final List<String> topics = logEvent.getTopics();
        if (topics == null || topics.size() == 0 || !topics.get(0).equals(signature)) {
            return null;
        }

        List<Type> indexedValues = Lists.newArrayList();
        List<Type> nonIndexedValues =
                FunctionReturnDecoder.decode(logEvent.getData(), event.getNonIndexedParameters());

        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value =
                    FunctionReturnDecoder.decodeIndexedValue(
                            topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        return new EventValues(indexedValues, nonIndexedValues);
    }

    private static <T> Event getEvent(Class<T> eventClass) {
        EventName nameAnnotation = eventClass.getAnnotation(EventName.class);
        if (Objects.isNull(nameAnnotation)) {
            throw new RuntimeException(eventClass.getSimpleName() + "找不到注解@EventName");
        }
        String eventName = nameAnnotation.value();
        List<TypeReference<?>> parameters = Lists.newArrayList();
        Field[] fields = eventClass.getDeclaredFields();
        for (Field field : fields) {
            EventField fieldAnnotation = field.getAnnotation(EventField.class);
            if (Objects.nonNull(fieldAnnotation)) {
                boolean indexed = fieldAnnotation.indexed();
                try {
                    Class<? extends Type> web3Type = field.getType().asSubclass(Type.class);
                    TypeReference<? extends Type> typeReference;
                    if (StaticArray.class.isAssignableFrom(web3Type) && !StaticStruct.class.isAssignableFrom(web3Type)) {
                        Class baseCls = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        String arraySize = web3Type.getSimpleName().split(StaticArray.class.getSimpleName())[1];
                        int arraySizeInt = Integer.parseInt(arraySize);
                        typeReference = makeStaticArrayReference(baseCls, indexed, arraySizeInt);
                    } else if (DynamicArray.class.isAssignableFrom(web3Type) && !DynamicStruct.class.isAssignableFrom(web3Type)) {
                        Class baseCls = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        typeReference = makeDynamicArrayReference(baseCls, indexed);

                    } else {
                        typeReference = TypeReference.create(web3Type, indexed);
                    }
                    parameters.add(typeReference);
                } catch (ClassCastException e) {
                    throw new ClassCastException(eventClass.getName() + "#" + field.getName() + "必须是org.web3j.abi.datatypes.Type子类");
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return new Event(eventName, parameters);
    }


    public static TypeReference<? extends Type> makeStaticArrayReference(Class baseCls, boolean indexed, int arraySizeInt) throws ClassNotFoundException {
        TypeReference baseTr = TypeReference.create(baseCls, indexed);
        Class arrayClass = StaticArray.class;
        return new TypeReference.StaticArrayTypeReference<StaticArray>(arraySizeInt) {

            TypeReference getSubTypeReference() {
                return baseTr;
            }

            @Override
            public boolean isIndexed() {
                return indexed;
            }

            @Override
            public java.lang.reflect.Type getType() {
                return new ParameterizedType() {
                    @Override
                    public java.lang.reflect.Type[] getActualTypeArguments() {
                        return new java.lang.reflect.Type[]{baseTr.getType()};
                    }

                    @Override
                    public java.lang.reflect.Type getRawType() {
                        return arrayClass;
                    }

                    @Override
                    public java.lang.reflect.Type getOwnerType() {
                        return Class.class;
                    }
                };
            }
        };
    }

    public static TypeReference<? extends Type> makeDynamicArrayReference(Class baseCls, boolean indexed) throws ClassNotFoundException {
        TypeReference baseTr = TypeReference.create(baseCls, indexed);
        Class arrayClass = DynamicArray.class;
        return new TypeReference<DynamicArray>(indexed) {

            TypeReference getSubTypeReference() {
                return baseTr;
            }

            @Override
            public java.lang.reflect.Type getType() {
                return new ParameterizedType() {
                    @Override
                    public java.lang.reflect.Type[] getActualTypeArguments() {
                        return new java.lang.reflect.Type[]{baseTr.getType()};
                    }

                    @Override
                    public java.lang.reflect.Type getRawType() {
                        return arrayClass;
                    }

                    @Override
                    public java.lang.reflect.Type getOwnerType() {
                        return Class.class;
                    }
                };
            }
        };
    }

}
