package com.example.redisinactions.infra;

import com.example.redisinactions.common.JoinPointSpELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
public class DistributedLockAspect {

  private final RedissonClient redissonClient;
  private final JoinPointSpELParser joinPointSpELParser;

  @Around("@annotation(distributedLock)")
  public Object lock(ProceedingJoinPoint pjp, DistributedLock distributedLock) throws Throwable {
    String pa = joinPointSpELParser.parseSpEL(pjp, distributedLock.key());
    final String key = distributedLock.lockConfig().generateKey(pa);
    RLock lock = redissonClient.getLock(key);
    try {
      final boolean isAcquired = lock.tryLock(distributedLock.lockConfig().waitTime, distributedLock.lockConfig().leaseTime,
          distributedLock.lockConfig().timeUnit);
      if (!isAcquired) {
        return null;
      }
      return pjp.proceed();
    } catch (RedisConnectionException redisUnavailableException) {
      log.warn("", redisUnavailableException);
      return pjp.proceed();
    } catch (InterruptedException e) {
      log.error("", e);
      Thread.currentThread().interrupt();
      return null;
    } finally {
      if (lock.isLocked() && lock.isHeldByCurrentThread()) {
        lock.unlock();
      }
    }
  }

}
