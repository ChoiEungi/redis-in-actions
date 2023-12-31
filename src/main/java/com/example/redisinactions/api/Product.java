  package com.example.redisinactions.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String description;

    private BigDecimal price;

    private Long quantity = 0L;

    public Product(String description, BigDecimal price) {
        this(description, price, 0L);
    }

    public Product(String description, BigDecimal price, Long quantity) {
        this(null, description, price, quantity);
    }

    private Product(Long id, String description, BigDecimal price, Long quantity){
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public void decrease(Long quantity) {
        this.quantity -= quantity;
        if (this.quantity < 0) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }
}
