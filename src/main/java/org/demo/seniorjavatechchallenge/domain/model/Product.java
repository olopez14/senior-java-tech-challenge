
package org.demo.seniorjavatechchallenge.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Product {
    private final Long id;
    private final ProductName name;
    private final ProductDescription description;
    private final List<Price> prices = new ArrayList<>();

    public Product(Long id, ProductName name, ProductDescription description) {
        if (name == null) throw new IllegalArgumentException("Name required");
        if (description == null) throw new IllegalArgumentException("Description required");
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Constructor para consultas/historial: permite asociar una lista arbitraria de precios
    public Product(Long id, ProductName name, ProductDescription description, List<Price> prices) {
        this(id, name, description);
        if (prices != null) {
            this.prices.addAll(prices);
        }
    }

    public Long getId() { return id; }
    public ProductName getName() { return name; }
    public ProductDescription getDescription() { return description; }

    public List<Price> getPrices() {
        return Collections.unmodifiableList(prices);
    }

    public void addPrice(Price price) {
        Objects.requireNonNull(price, "Price required");
        for (Price existing : prices) {
            if (existing.getDateRange().overlaps(price.getDateRange())) {
                throw new IllegalArgumentException("Price date range overlaps with existing price");
            }
        }
        prices.add(price);
    }
}



