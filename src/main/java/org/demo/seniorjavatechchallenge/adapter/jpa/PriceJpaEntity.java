package org.demo.seniorjavatechchallenge.adapter.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;


@Entity
@Table(name = "prices", indexes = {
        @Index(name = "idx_prices_product", columnList = "product_id"),
        @Index(name = "idx_prices_product_init", columnList = "product_id, init_date"),
        @Index(name = "idx_prices_product_end", columnList = "product_id, end_date")
})
@Getter
@Setter
public class PriceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.persistence.ManyToOne(optional = false)
    @jakarta.persistence.JoinColumn(name = "product_id", nullable = false)
    private ProductJpaEntity product;

    @Column(name = "price_value", nullable = false)
    private BigDecimal value;

    @Column(name = "init_date", nullable = false)
    private LocalDate initDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public PriceJpaEntity() {
    }

    public PriceJpaEntity(ProductJpaEntity product, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.product = product;
        this.value = value;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    
    public Price toDomain() {
        Price price = new Price(
            product != null ? product.toDomain() : null,
            this.value,
            this.initDate,
            this.endDate
        );
        price.setId(this.id);
        return price;
    }

    
    public static PriceJpaEntity fromDomain(Price domain, ProductJpaEntity productJpaEntity) {
        PriceJpaEntity entity = new PriceJpaEntity(productJpaEntity,
                domain.getValue(), domain.getInitDate(), domain.getEndDate());
        entity.id = domain.getId();
        return entity;
    }

    
    // Lombok provides getters and setters
}



