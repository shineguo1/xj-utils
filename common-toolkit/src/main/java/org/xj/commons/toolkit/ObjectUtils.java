package org.xj.commons.toolkit;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/14 18:09
 */
public class ObjectUtils {


    /**
     * copy from org.apache.commons.lang3.ObjectUtils
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }
}
