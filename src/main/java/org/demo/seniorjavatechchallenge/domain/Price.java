package org.demo.seniorjavatechchallenge.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

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

@Entity
@Table(name = "prices", indexes = {
        @Index(name = "idx_prices_product_init", columnList = "product_id, init_date"),
        @Index(name = "idx_prices_product_end", columnList = "product_id, end_date")
})
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal value;

    @Column(name = "init_date", nullable = false)
    private LocalDate initDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    protected Price() {
    }

    public Price(Product product, BigDecimal value, LocalDate initDate, LocalDate endDate) {
        this.product = product;
        this.value = value;
        this.initDate = initDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public BigDecimal getValue() {
        return value;
    }

    public LocalDate getInitDate() {
        return initDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}

