package com.example.redisinactions.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.example.redisinactions.config.CacheConfig.CacheName.PRODUCT_CACHE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @Cacheable(value = PRODUCT_CACHE, key = "'top10'")
    public List<ProductResponse> getTenProduct() {
        log.warn("NO CACHE - find top 10 products from DB");
        return ProductResponse.listOf(productRepository.findTop10By());
    }

    @Transactional
    public void saveProductMock(){
        productRepository.save(new Product("test", new BigDecimal(1000)));
    }
}
