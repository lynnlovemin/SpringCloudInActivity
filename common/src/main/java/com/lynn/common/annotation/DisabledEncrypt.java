package com.lynn.common.annotation;

import java.lang.annotation.*;

/**
 * 禁用加解密
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisabledEncrypt {
}
