package com.troublemaker.clockin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.annotation
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  20:53
 * @Version: 1.0
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JWTToken {
    boolean required() default true;
}
