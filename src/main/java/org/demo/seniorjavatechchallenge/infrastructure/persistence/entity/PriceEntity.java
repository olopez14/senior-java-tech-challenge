package org.demo.seniorjavatechchallenge.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PriceEntity {

    private Long id;
    private Long productId;
    private BigDecimal priceValue;
    private LocalDate initDate;
    private LocalDate endDate;

    public PriceEntity() {}

    public PriceEntity(Long id, Long productId, BigDecimal priceValue, LocalDate initDate, LocalDate endDate) {
        this.id = id;
        this.productId = productId;
        this.priceValue = priceValue;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(BigDecimal priceValue) {
        this.priceValue = priceValue;
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

