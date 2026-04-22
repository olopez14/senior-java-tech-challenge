package org.demo.seniorjavatechchallenge.infrastructure.persistence.adapters;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.model.Price;
import org.demo.seniorjavatechchallenge.domain.model.Product;
import org.demo.seniorjavatechchallenge.domain.repository.PriceRepository;
import org.demo.seniorjavatechchallenge.domain.repository.ProductRepository;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.PriceEntity;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.repository.PriceJdbcRepository;
import org.demo.seniorjavatechchallenge.infrastructure.persistence.mapper.PersistencePriceMapper;
import org.springframework.stereotype.Component;


@Component
public class PriceRepositoryAdapter implements PriceRepository {

    private final PriceJdbcRepository priceJdbcRepository;
    private final ProductRepository productRepository;

    public PriceRepositoryAdapter(PriceJdbcRepository priceJdbcRepository, ProductRepository productRepository) {
        this.priceJdbcRepository = priceJdbcRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Price> findByProductIdOrderByInitDateAsc(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product with id %d not found".formatted(productId)));
        List<PriceEntity> entities = priceJdbcRepository.findByProductIdOrderByInitDateAsc(productId);
        return entities.stream().map(e -> PersistencePriceMapper.toDomain(e, product)).toList();
    }


    @Override
    public Optional<BigDecimal> findCurrentPriceValue(Long productId, LocalDate date) {
        return priceJdbcRepository.findCurrentPriceValue(productId, date);
    }

    @Override
    public java.util.List<Price> findOverlappingPrices(Long productId, LocalDate initDate, LocalDate endDate) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product with id %d not found".formatted(productId)));
        var entities = priceJdbcRepository.findOverlappingPrices(productId, initDate, endDate);
        return entities.stream().map(e -> PersistencePriceMapper.toDomain(e, product)).toList();
    }

    @Override
    public Price save(Price price) {
        if (price.getProduct() == null || price.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product not found");
        }
        var entity = PersistencePriceMapper.toEntity(price);
        var saved = priceJdbcRepository.save(entity);
        return PersistencePriceMapper.toDomain(saved, price.getProduct());
    }
}
