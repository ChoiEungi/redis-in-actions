package com.example.redisinactions.infra;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface DistributedLock {

  String key();

  LockConfig lockConfig();

  boolean isTransactionEnabled() default false;

}