package com.example.redisinactions.lock;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.redisinactions.api.Product;
import com.example.redisinactions.api.ProductRepository;
import com.example.redisinactions.api.v2.V2ProductService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class V2ProductServiceTest {

  @Autowired
  private V2ProductService v2ProductService;

  @Autowired
  private ProductRepository productRepository;

  private Product product;

  @BeforeEach
  void setUp() {
    product = productRepository.save(new Product("description", new BigDecimal(10000), 100L));
  }

  @Test
  void decreaseWithAOP() {
    // given
    int requestCount = 10;

    // when
    List<CompletableFuture<?>> futureList = executeAsync(() -> v2ProductService.decreaseWithAOP(product.getId(), 1L), requestCount);
    futureList.forEach(CompletableFuture::join);

    // then
    Product result = productRepository.findById(product.getId()).orElseThrow();
    assertThat(result.getQuantity()).isEqualTo(90L);


  }

  @Test
  void decreaseWithAOPV2() {
    // given
    int requestCount = 10;

    // when
    List<CompletableFuture<?>> futureList = executeAsync(() -> v2ProductService.decreaseWithAOPV2(product.getId(), 1L), requestCount);
    futureList.forEach(CompletableFuture::join);

    // then
    Product result = productRepository.findById(product.getId()).orElseThrow();
    assertThat(result.getQuantity()).isEqualTo(90L);


  }

  @Test
  void decreaseWithCallback() {
    // given
    int requestCount = 10;

    // when
    List<CompletableFuture<?>> futureList = executeAsync(() -> v2ProductService.decreaseWithCallback(product.getId(), 1L), requestCount);
    futureList.forEach(CompletableFuture::join);

    // then
    Product result = productRepository.findById(product.getId()).orElseThrow();
    assertThat(result.getQuantity()).isEqualTo(90L);

  }

  @Test
  void decreaseWithCallbackTransaction() {
    // given
    int requestCount = 10;

    // when
    List<CompletableFuture<?>> futureList = executeAsync(() -> v2ProductService.decreaseWithCallbackTransaction(product.getId(), 1L), requestCount);
    futureList.forEach(CompletableFuture::join);

    // then
    Product result = productRepository.findById(product.getId()).orElseThrow();
    assertThat(result.getQuantity()).isEqualTo(90L);

  }

  private List<CompletableFuture<?>> executeAsync(Runnable callback, int requestCount) {
    List<CompletableFuture<?>> futureList = new ArrayList<>();
    for (int i = 0; i < requestCount; i++) {
      CompletableFuture<Object> objectCompletableFuture = CompletableFuture.supplyAsync(() -> {
        callback.run();
        return null;
      });
      futureList.add(objectCompletableFuture);
    }
    return futureList;

  }
}