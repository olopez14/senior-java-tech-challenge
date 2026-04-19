package org.demo.seniorjavatechchallenge.adapter.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.demo.seniorjavatechchallenge.domain.Price;
import org.demo.seniorjavatechchallenge.domain.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Adaptador JPA para entidad Price.
 * Mapea Price de dominio a persistencia en BD.
 */
@Entity
@Table(name = "prices", indexes = {
        @Index(name = "idx_prices_product", columnList = "product_id"),
        @Index(name = "idx_prices_product_init", columnList = "product_id, init_date"),
        @Index(name = "idx_prices_product_end", columnList = "product_id, end_date")
})
public class PriceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
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

    // Conversión: JPA → Dominio
    public Price toDomain() {
        Product domainProduct = this.product.toDomain();
        Price price = new Price(domainProduct, this.value, this.initDate, this.endDate);
        price.setId(this.id);
        return price;
    }

    // Conversión: Dominio → JPA
    public static PriceJpaEntity fromDomain(Price domain, ProductJpaEntity product) {
        PriceJpaEntity entity = new PriceJpaEntity(product, domain.getValue(),
                                                   domain.getInitDate(), domain.getEndDate());
        entity.id = domain.getId();
        return entity;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductJpaEntity getProduct() {
        return product;
    }

    public void setProduct(ProductJpaEntity product) {
        this.product = product;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public void setInitDate(LocalDate initDate) {
        this.initDate = initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

