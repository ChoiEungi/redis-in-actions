package com.example.redisinactions.infra;

import java.util.concurrent.TimeUnit;
import org.springframework.util.StringUtils;

public enum LockConfig {

  DEFUALT(10L, 1L, TimeUnit.SECONDS),
  PRODUCT_DECREASE("PRODUCT", 10L, 1L, TimeUnit.SECONDS),
  LOGIN_LOCK("PRODUCT", 10L, 1L, TimeUnit.SECONDS);

  public final Long waitTime;
  public final Long leaseTime;
  public final TimeUnit timeUnit;
  private final String lockPrefix;

  LockConfig(Long waitTime, Long leaseTime, TimeUnit timeUnit) {
    this.lockPrefix = String.format("%s_%s", this.getClass().getName(), this.name());
    this.waitTime = waitTime;
    this.leaseTime = leaseTime;
    this.timeUnit = timeUnit;
  }

  LockConfig(String lockPrefix, Long waitTime, Long leaseTime, TimeUnit timeUnit) {
    this.lockPrefix = lockPrefix;
    this.waitTime = waitTime;
    this.leaseTime = leaseTime;
    this.timeUnit = timeUnit;
  }

  public String generateKey(String key) {
    if (!StringUtils.hasText(key)) {
      throw new IllegalArgumentException("key must not be empty");
    }
    return String.format("%s_%s", lockPrefix, key);
  }


}