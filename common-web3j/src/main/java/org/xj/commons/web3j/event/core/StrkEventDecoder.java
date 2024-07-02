package org.xj.commons.web3j.event.core;

import com.alibaba.fastjson2.JSON;
import org.xj.commons.web3j.abi.StrkEncoder;
import org.xj.commons.web3j.abi.StrkTypeDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/13 13:38
 */
@Getter
@Slf4j
public class StrkEventDecoder<T> extends EventDecoder<T> {

    protected StrkEventDecoder(Class<T> eventClass) {
        super(eventClass);
    }


    /**
     * copy了这个方法 {@link Contract#staticExtractEventParameters}
     * 修改了signature判断。因为在本系统中，存在event只知道topic不知道EventName
     *
     * @param logEvent
     * @return
     */
    @Override
    protected EventValues getEventValues(Log logEvent) {
        final List<String> topics = logEvent.getTopics();
        if (topics == null || topics.size() == 0 || !topics.get(0).equals(signature)) {
            return null;
        }

        // indexedValues 适配 Starknet Event 的 "keys: String[]" 里的值
        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        List<String> topicsAfter0 = topics.subList(1, topics.size());
        List<Type> indexedValues = StrkTypeDecoder.decode(topicsAfter0, indexedParameters);

        // nonIndexed不适用，适配 Starknet Event 的 "data: String[]" 里的值.
        // 在StrkHttpService适配器中, 将data数组以jsonString格式存在LogObject.data字段中
        String jsonValueOfData = logEvent.getData();
        List<String> data = JSON.parseArray(jsonValueOfData, String.class);
        List<TypeReference<Type>> nonIndexedParameters = event.getNonIndexedParameters();
        List<Type> nonIndexedValues = StrkTypeDecoder.decode(data, nonIndexedParameters);

        return new EventValues(indexedValues, nonIndexedValues);
    }

    @Override
    protected <T> String getSignature(Class<T> eventClass, Event event) {
        EventName nameAnnotation = eventClass.getAnnotation(EventName.class);
        return StrkEncoder.buildEventSignature(nameAnnotation);
    }
}