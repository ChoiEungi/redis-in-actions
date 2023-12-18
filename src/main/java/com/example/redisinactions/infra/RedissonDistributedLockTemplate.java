package com.example.redisinactions.infra;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedissonDistributedLockTemplate<T> {

  private final RedissonClient redissonClient;

  public T executeWithLockIfNotAcquiredFailed(String key, LockConfig lockConfig, Supplier<T> callback) {
    RLock lock = redissonClient.getLock(lockConfig.generateKey(key));

    try {
      // waitTime 동안 lock 획득을 시도하고, lock 획득에 실패하면 false 를 반환한다.
      boolean isAcquired = lock.tryLock(lockConfig.waitTime, lockConfig.leaseTime, lockConfig.timeUnit);
      if (!isAcquired) {
        log.warn("lock 획득 실패");
        return null;
      }
      return callback.get();
    } catch (InterruptedException e) {
      log.error("", e);
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }

  public T executeTransactionWithLock(String key, LockConfig lockConfig, Supplier<T> callback) {
    RLock lock = redissonClient.getLock(lockConfig.generateKey(key));

    try {
      boolean available = lock.tryLock(lockConfig.waitTime, lockConfig.leaseTime, lockConfig.timeUnit);
      if (!available) {
        log.warn("lock 획득 실패");
        return null;
      }
      return callback.get();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
  }
}
