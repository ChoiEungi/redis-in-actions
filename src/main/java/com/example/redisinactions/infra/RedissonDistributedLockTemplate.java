package com.example.redisinactions.infra;

import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisConnectionException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@Slf4j
public class RedissonDistributedLockTemplate {

  private final RedissonClient redissonClient;
  private final TransactionTemplate transactionTemplate;

  public RedissonDistributedLockTemplate(RedissonClient redissonClient, PlatformTransactionManager platformTransactionManager) {
    this.redissonClient = redissonClient;
    this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    this.transactionTemplate.afterPropertiesSet();
  }

  public void executeWithLock(String key, LockConfig lockConfig, Runnable callback) {
    executeWithLock(key, lockConfig, toVoidSupplier(callback));
  }

  public <T> T executeWithLock(String key, LockConfig lockConfig, Supplier<T> callback) {
    RLock lock = redissonClient.getLock(lockConfig.generateKey(key));

    try {
      boolean isAcquired = lock.tryLock(lockConfig.waitTime, lockConfig.leaseTime, lockConfig.timeUnit);
      if (!isAcquired) {
        log.warn("[lock 획득 실패] {}, key : {}", lockConfig, key);
        return null;
      }
      return callback.get();
    } catch (RedisConnectionException redisUnavailableException) {
      log.warn("", redisUnavailableException);
      return callback.get();
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

  public void executeWithLockAndTransaction(String key, LockConfig lockConfig, Runnable callback) {
    executeWithLockAndTransaction(key, lockConfig, toVoidSupplier(callback));
  }

  public <T> T executeWithLockAndTransaction(String key, LockConfig lockConfig, Supplier<T> callback) {
    RLock lock = redissonClient.getLock(lockConfig.generateKey(key));

    try {
      boolean isAcquired = lock.tryLock(lockConfig.waitTime, lockConfig.leaseTime, lockConfig.timeUnit);
      if (!isAcquired) {
        log.warn("[lock 획득 실패] {}, key : {}", lockConfig, key);
        return null;
      }
      return transactionTemplate.execute(status -> callback.get());
    } catch (RedisConnectionException redisUnavailableException) {
      log.warn("", redisUnavailableException);
      return transactionTemplate.execute(status -> callback.get());
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

  private Supplier<Void> toVoidSupplier(Runnable runnable) {
    return () -> {
      runnable.run();
      return null;
    };
  }

}
