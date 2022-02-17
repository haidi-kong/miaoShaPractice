package com.travel.admin.config.mvc;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author luo
 */

@Retention(RUNTIME)
@Target(METHOD)
public @interface IpLimit {

    String limitCount();

    String limitTime();
}
