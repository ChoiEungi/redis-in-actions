package com.example.redisinactions.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products/top10")
    public ResponseEntity<?> getTop10Products() {
        return ResponseEntity.ok(productService.getTenProduct());
    }

    @PostMapping("/products")
    public ResponseEntity<?> saveProductMock() {
        productService.saveProductMock();
        return ResponseEntity.ok().build();
    }
}
