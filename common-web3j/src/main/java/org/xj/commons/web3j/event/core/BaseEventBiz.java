package org.xj.commons.web3j.event.core;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.event.model.ContractParseContext;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.core.methods.response.Log;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 事件容器类 基类
 *
 * @author xj
 * @date 2022/4/7
 */

@Slf4j
public abstract class BaseEventBiz<R, Context extends ContractParseContext<?, R>> {

    /**
     * 缓存 signature => eventSolver
     */
    protected Map<String, Class> eventClassSignatureCache = CollectionUtils.toMap(listenEvents(),
            eventClass -> EventDecoder.create(eventClass).getSignature(), eventClass -> eventClass, (o1, o2) -> o1);


    public Set<String> getEventTopics() {
        return eventClassSignatureCache.keySet();
    }

    /**
     * 通过topic匹配eventWrapper
     */
    public EventDecoder match(String topic) {
        Class eventClass = eventClassSignatureCache.get(topic);
        return Objects.nonNull(eventClass) ? EventDecoder.create(eventClass) : null;
    }

    public Boolean contains(String topic) {
        return eventClassSignatureCache.containsKey(topic);
    }

    public EventDecoder decoder(Log logs) {
        List<String> topics = logs.getTopics();
        if (CollectionUtils.isEmpty(topics)) {
            return null;
        }
        String eventSignature = topics.get(0);
        return match(eventSignature);
    }

    public List<EventDecoder> decoder(List<Log> logs) {
        return logs.stream().map(this::decoder).collect(Collectors.toList());
    }


    protected String toStringIfNotNull(Object o) {
        return o == null ? null : o.toString();
    }


    /* ============ 需要实现的接口 ============= */

    /**
     * @return 监听的事件类
     */
    abstract protected Collection<Class> listenEvents();

    /**
     * 当1个区块，只有个别事件，或不需要查链，建议使用下面这个方法解析（代码简单），继承{@link AbstractOneEventBiz}
     * 当1个区块，存在多数事件，并且需要查链（有合并请求需要），建议使用{@link #parse(List)}解析，继承{@link AbstractListEventBiz}
     * <p>
     * 处理事件，需要实现幂等性
     */
    public abstract void parse(Context decodeLogContext);

    /**
     * 当1个区块，只有个别事件，或不需要查链，建议使用{@link #parse(ContractParseContext)}解析（代码简单），继承{@link AbstractOneEventBiz}
     * 当1个区块，存在多数事件，并且需要查链（有合并请求需要），建议使用下面这个方法解析，继承{@link AbstractListEventBiz}
     * <p>
     * 处理事件，需要实现幂等性
     */
    public abstract void parse(List<Context> decodeLogContext);


}

