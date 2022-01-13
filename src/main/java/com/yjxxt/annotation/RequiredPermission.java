package com.yjxxt.annotation;


import java.lang.annotation.*;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiredPermission {
    String code() default "";
}
