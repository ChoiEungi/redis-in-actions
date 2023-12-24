package com.example.redisinactions.api.v2;

import static com.example.redisinactions.infra.LockConfig.PRODUCT_DECREASE;

import com.example.redisinactions.api.Product;
import com.example.redisinactions.api.ProductRepository;
import com.example.redisinactions.infra.DistributedLock;
import com.example.redisinactions.infra.RedissonDistributedLockTemplate;
import com.example.redisinactions.infra.V2DistributedLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class V2ProductService {

  private final ProductRepository productRepository;
  private final RedissonDistributedLockTemplate redissonDistributedLockTemplate;

  @DistributedLock(key = "#id", lockConfig = PRODUCT_DECREASE)
  public Product decreaseWithAOP(Long id, Long quantity) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decrease(quantity);
    return productRepository.save(product);
  }

  @V2DistributedLock(key = "#id", lockConfig = PRODUCT_DECREASE, isTransactionEnabled = true)
  public Product decreaseWithAOPV2(Long id, Long quantity) {
    Product product = productRepository.findById(id).orElseThrow();
    product.decrease(quantity);
    return product;
  }

  public Product decreaseWithCallback(Long id, Long quantity) {
    Product result = redissonDistributedLockTemplate.executeWithLock(id.toString(), PRODUCT_DECREASE, () -> {
      Product product = productRepository.findById(id).orElseThrow();
      product.decrease(quantity);
      return productRepository.save(product);
    });
    return result;
  }

  public Product decreaseWithCallbackTransaction(Long id, Long quantity) {
    Product result = redissonDistributedLockTemplate.executeWithLockAndTransaction(id.toString(), PRODUCT_DECREASE, () -> {
      Product product = productRepository.findById(id).orElseThrow();
      product.decrease(quantity);
      return product;
    });
    return result;
  }

}
