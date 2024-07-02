package org.xj.commons.web3j.sideoutput;

import org.xj.commons.toolkit.ReflectionUtils;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/12 10:55
 */
public interface SideOutputFunction {

    Map sideOutputsMap = Maps.newHashMap();

    /**
     * 侧流输出
     *
     * @param outputTag 侧流输出tag
     * @param <V>       侧流输出类型
     * @return
     */
    default <V> V getValueSideOutput(ValueOutputTag<V> outputTag) {
        V obj = (V) sideOutputsMap.get(outputTag.getInternalKey());
        if (obj == null) {
            obj = ReflectionUtils.newInstance(outputTag.getValueClass());
            sideOutputsMap.put(outputTag.getInternalKey(), obj);
        }
        return obj;
    }

    /**
     * 侧流输出
     *
     * @param outputTag 侧流输出tag
     * @param <V>       侧流输出类型
     * @return
     */
    default <V> Map<Object, V> getMapSideOutput(MapOutputTag<V> outputTag) {
        Map<Object, V> obj = (Map<Object, V>) sideOutputsMap.get(outputTag.getInternalKey());
        if (obj == null) {
            sideOutputsMap.put(outputTag.getInternalKey(), Maps.newHashMap());
        }
        return obj;
    }
}
