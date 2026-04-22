package org.demo.seniorjavatechchallenge.application.service;

import org.demo.seniorjavatechchallenge.application.dto.response.ProductPriceHistoryResponse;
import org.demo.seniorjavatechchallenge.application.mapper.ProductMapper;
import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GetProductPriceHistoryService {
    private final PriceRepository priceRepository;
    private final FindProductService findProductService;

    public GetProductPriceHistoryService(PriceRepository priceRepository, FindProductService findProductService) {
        this.priceRepository = priceRepository;
        this.findProductService = findProductService;
    }

    @Transactional(readOnly = true)
    public ProductPriceHistoryResponse execute(Long productId) {
        Product product = findProductService.execute(productId);
        List<Price> prices = priceRepository.findByProductIdOrderByInitDateAsc(productId);
        Product productWithPrices = new Product(
            product.getId(),
            product.getName(),
            product.getDescription(),
            prices
        );
        return ProductMapper.toHistoryResponse(productWithPrices);
    }
}
