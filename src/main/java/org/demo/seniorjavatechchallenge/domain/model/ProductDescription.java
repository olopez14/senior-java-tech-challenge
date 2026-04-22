package org.demo.seniorjavatechchallenge.domain.model;

public final class ProductDescription {
    private final String value;

    public ProductDescription(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Product description required");
        this.value = value.trim();
    }

    public String getValue() { return value; }

    @Override
    public String toString() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDescription that = (ProductDescription) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return value.hashCode(); }
}

