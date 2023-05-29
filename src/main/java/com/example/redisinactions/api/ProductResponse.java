package com.example.redisinactions.api;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse implements Serializable {
    private String description;
    private BigDecimal price;

    private static ProductResponse of(Product product) {
        return new ProductResponse(product.getDescription(), product.getPrice());
    }

    public static List<ProductResponse> listOf(List<Product> productList) {
        return productList.stream()
                .map(ProductResponse::of)
                .toList();
    }
}
