package org.demo.seniorjavatechchallenge.service;

import java.util.List;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;
import org.demo.seniorjavatechchallenge.dto.request.CreateProductRequest;
import org.demo.seniorjavatechchallenge.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.dto.response.ProductResponse;
import org.demo.seniorjavatechchallenge.exception.ProductNotFoundException;
import org.demo.seniorjavatechchallenge.mapper.ProductMapper;
import org.demo.seniorjavatechchallenge.repository.PriceRepository;
import org.demo.seniorjavatechchallenge.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final PriceRepository priceRepository;

    public ProductService(ProductRepository productRepository, PriceRepository priceRepository) {
        this.productRepository = productRepository;
        this.priceRepository = priceRepository;
    }

    
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = ProductMapper.toProduct(request);
        Product savedProduct = productRepository.save(product);
        return ProductMapper.toResponse(savedProduct);
    }

    
    @Transactional(readOnly = true)
    public ProductPriceHistoryResponse getProductPriceHistory(Long productId) {
        
        Product product = findProductOrThrow(productId);

        
        List<Price> prices = priceRepository.findByProductIdOrderByInitDateAsc(productId);
        product.setPrices(prices);

        return ProductMapper.toHistoryResponse(product);
    }

    
    @Transactional(readOnly = true)
    public Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    
    @Transactional
    public Product findProductForUpdateOrThrow(Long productId) {
        return productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}




