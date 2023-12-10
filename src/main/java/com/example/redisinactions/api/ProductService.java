package com.example.redisinactions.api;

import com.example.redisinactions.api.v2.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

import static com.example.redisinactions.config.CacheConfig.CacheName.PRODUCT_CACHE;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @PostConstruct
    void initProducts() {
        productRepository.saveAll(List.of(
                        new Product("box", new BigDecimal(1000)),
                        new Product("snack", new BigDecimal(4000)),
                        new Product("chicken", new BigDecimal(20000))
                )
        );
    }

    @Cacheable(cacheNames = PRODUCT_CACHE, key = "'top10'")
    public List<ProductResponse> getTenProduct() {
        log.warn("NO CACHE - find top 10 products from DB");
        return ProductResponse.listOf(productRepository.findTop10By());
    }

    @CacheEvict(cacheNames = PRODUCT_CACHE, key = "'top10'")
    public void evict() {
        log.warn("Cache Evicted");
    }

}
