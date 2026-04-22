package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.application.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.application.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.application.mapper.ProductMapper;
import org.demo.seniorjavatechchallenge.domain.exception.PreconditionViolationException;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductService {
    private final ProductRepository productRepository;

    public CreateProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse execute(CreateProductRequest request) {
        if (request == null) throw new PreconditionViolationException("CreateProductRequest no puede ser null");
        if (request.name() == null) throw new PreconditionViolationException("name no puede ser null");
        if (request.description() == null) throw new PreconditionViolationException("description no puede ser null");
        Product product = ProductMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponse(savedProduct);
    }
}
