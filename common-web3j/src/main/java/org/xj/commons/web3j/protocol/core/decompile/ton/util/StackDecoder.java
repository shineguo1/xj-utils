package org.xj.commons.web3j.protocol.core.decompile.ton.util;

import com.alibaba.fastjson2.TypeReference;
import org.xj.commons.toolkit.ReflectionUtils;
import org.xj.commons.web3j.protocol.core.decompile.ton.address.TonAddress;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.TonType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 15:30
 */
public class StackDecoder {

    public static <RES> RES decode(List<TonType> stack, TypeReference<RES> typeReference) {
        Class<RES> rawType = (Class<RES>) typeReference.getRawType();
        return decode(stack, rawType);
    }

    @NotNull
    public static <RES> RES decode(List<TonType> stack, Class<RES> rawType) {
        Set<Field> fields = ReflectionUtils.getFields(rawType);
        Iterator<TonType> iterator = stack.iterator();
        //构造返回对象
        RES ret = (RES) ReflectionUtils.newInstance(rawType);
        //遍历field赋值
        for (Field field : fields) {
            TonType element = iterator.next();
            Class<?> fieldType = field.getType();
            Object value;
            //取值
            if (element.isNum()) {
                value = decodeNum(element, fieldType);
            } else if (element.isCell()) {
                value = decodeCell(element, fieldType);
            } else if (element.isTuple()) {
                value = decodeTuple(element, fieldType);
            } else {
                throw new RuntimeException("unknown type:" + element.getType());
            }
            //赋值
            try {
                field.setAccessible(true);
                field.set(ret, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return ret;
    }

    private static Object decodeTuple(TonType element, Class<?> fieldType) {
        if (List.class.isAssignableFrom(fieldType)) {
            //tuple->array: 以数组展开
            return element.getValueAsTuple();
        } else {
            //tuple->struct: 以结构体展开
            List<TonType> stack = element.getTuple();
            return decode(stack, fieldType);
        }
    }

    private static Object decodeCell(TonType element, Class<?> fieldType) {
        Cell[] valueAsCell = element.getValueAsCell();
        if (TonAddress.class.isAssignableFrom(fieldType)) {
            //cell[]->cell->address: cell的root只有1个元素，且是address
            return valueAsCell[0].readAddress();
        } else if (Cell.class.isAssignableFrom(fieldType)) {
            //cell[]->cell: cell的root只有一个元素
            return valueAsCell[0];
        } else {
            return valueAsCell;
        }
    }

    private static Object decodeNum(TonType element, Class<?> fieldType) {
        BigInteger valueAsNum = element.getValueAsNum();
        if (BigInteger.class.isAssignableFrom(fieldType)) {
            return valueAsNum;
        } else if (Integer.class.isAssignableFrom(fieldType) || int.class.isAssignableFrom(fieldType)) {
            return valueAsNum.intValue();
        } else if (Long.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
            return valueAsNum.longValue();
        } else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType)) {
            //num->bool: 0表示false, 非0表示true
            return !valueAsNum.equals(BigInteger.ZERO);
        } else if (String.class.isAssignableFrom(fieldType)) {
            //num->string: 无前缀hex
            return valueAsNum.toString(16);
        } else {
            throw new RuntimeException("can not cast `num` to type(" + fieldType.getName() + ")");
        }
    }

}
