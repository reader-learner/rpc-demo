package com.rpc.annotation;

import java.lang.annotation.*;

/**
 * 标注服务实现类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited //子类可以继承该注解，实现类不用也可以继承接口的注解
public @interface RPCService {
    String group() default "";

    String version() default "";
}
