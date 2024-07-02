package org.xj.commons.web3j.event.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xj
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EventName {

    String value() default "";

    String signature() default "";

    /**
     * 1. 缺省：表示evm, 映射实现类 {@link EventDecoder}
     * 2. Starknet || STRK : 表示Starknet链，映射实现类 {@link StrkEventDecoder}
     */
    String chainType() default "";
}
