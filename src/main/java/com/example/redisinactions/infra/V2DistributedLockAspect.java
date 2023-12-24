package com.example.redisinactions.infra;

import com.example.redisinactions.common.JointPointSpELParser;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class V2DistributedLockAspect {

  private final JointPointSpELParser jointPointSpELParser;
  private final RedissonDistributedLockTemplate redissonDistributedLockTemplate;

  @Around("@annotation(v2DistributedLock)")
  public Object lock(ProceedingJoinPoint pjp, V2DistributedLock v2DistributedLock) {
    String parsedKey = jointPointSpELParser.parseSpEL(pjp, v2DistributedLock.key());
    final String key = v2DistributedLock.lockConfig().generateKey(parsedKey);

    if (v2DistributedLock.isTransactionEnabled()) {
      return redissonDistributedLockTemplate.executeWithLockAndTransaction(key, v2DistributedLock.lockConfig(), proceed(pjp));
    }

    return redissonDistributedLockTemplate.executeWithLock(key, v2DistributedLock.lockConfig(), proceed(pjp));
  }

  private Supplier<Object> proceed(ProceedingJoinPoint pjp) {
    return () -> {
      try {
        return pjp.proceed();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

}
