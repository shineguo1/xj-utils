package org.xj.commons.toolkit;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * @author xinjie_guo
 * @version 1.0.0 createTime:  2023/7/17 20:09
 */
public class ResListUtils {

    //结构型
    //====================================

    @NotNull
    public static BigInteger getNumberFromList(List<?> list, int index) {
        return CollectionUtils.sizeOf(list) <= index ? BigInteger.ZERO :
                BigIntegerUtils.defaultZero((BigInteger) list.get(index));
    }

    @Nullable
    public static BigInteger getNumberFromListDefaultNull(List<?> list, int index) {
        return getObjFromList(list, index);
    }

    @NotNull
    public static <T> List<T> getListFromList(List<?> list, int index) {
        return CollectionUtils.sizeOf(list) <= index ? Collections.emptyList() :
                ObjectUtils.defaultIfNull((List<T>) list.get(index), Collections.emptyList());
    }

    @Nullable
    public static <T> T getObjFromList(List<?> list, int index) {
        return CollectionUtils.sizeOf(list) <= index ? null : (T) list.get(index);
    }

    @Nullable
    public static String toString(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return String.valueOf(o);
        }
    }
}
