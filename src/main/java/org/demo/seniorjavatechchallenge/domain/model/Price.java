
package org.demo.seniorjavatechchallenge.domain.model;

import java.util.Objects;

public class Price {
    private final Long id;
    private final Product product;
    private final Money value;
    private final DateRange dateRange;

    // Constructor para nuevos precios (sin id)
    public Price(Product product, Money value, DateRange dateRange) {
        this(null, product, value, dateRange);
    }

    // Constructor para precios existentes (con id)
    public Price(Long id, Product product, Money value, DateRange dateRange) {
        if (product == null) throw new IllegalArgumentException("Product required");
        if (value == null) throw new IllegalArgumentException("Price value required");
        if (dateRange == null) throw new IllegalArgumentException("Date range required");
        this.id = id;
        this.product = product;
        this.value = value;
        this.dateRange = dateRange;
    }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public Money getValue() { return value; }
    public DateRange getDateRange() { return dateRange; }
}





