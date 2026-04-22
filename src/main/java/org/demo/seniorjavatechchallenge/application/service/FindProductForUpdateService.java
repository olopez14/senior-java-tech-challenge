package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.ProductRepository;
import org.demo.seniorjavatechchallenge.domain.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindProductForUpdateService {
    private final ProductRepository productRepository;

    public FindProductForUpdateService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(Long productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}


