package com.example.redisinactions.infra;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedissonDistributedLockTemplate {

  private final RedissonClient redissonClient;

  public void executeWithLock(String key, LockConfig lockConfig, Runnable callback) {
    executeWithLock(key, lockConfig, toVoidSupplier(callback));
  }

  public <T> T executeWithLock(String key, LockConfig lockConfig, Supplier<T> callback) {
    RLock lock = redissonClient.getLock(lockConfig.generateKey(key));

    try {
      boolean isAcquired = lock.tryLock(lockConfig.waitTime, lockConfig.leaseTime, lockConfig.timeUnit);
      if (!isAcquired) {
        log.warn("[lock 획득 실패] {}, key : {}" , lockConfig, key);
        return null;
      }
      return callback.get();
    } catch (RedisConnectionException redisUnavailableException) {
      log.warn("", redisUnavailableException);
      return callback.get();
    } catch (InterruptedException e) {
      log.error("", e);
      throw new RuntimeException(e);
    } finally {
      if(lock.isLocked() && lock.isHeldByCurrentThread()){
        lock.unlock();
      }
    }
  }

  private Supplier<Void> toVoidSupplier(Runnable runnable) {
    return () -> {
      runnable.run();
      return null;
    };
  }

}
