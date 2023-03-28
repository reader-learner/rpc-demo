package com.rpc.annotation;

import java.lang.annotation.*;

/**
 * 自动装配
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface RPCAutowire {
    String serverName() default "";

    String group() default "";

    String version() default "";
}
