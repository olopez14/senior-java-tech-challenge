package org.demo.seniorjavatechchallenge.adapter.jpa;

import java.util.ArrayList;
import java.util.List;

import org.demo.seniorjavatechchallenge.domain.Product;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Adaptador JPA para entidad Product.
 * Mapea Product de dominio a persistencia en BD.
 */
@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceJpaEntity> prices = new ArrayList<>();

    public ProductJpaEntity() {
    }

    public ProductJpaEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Conversión: JPA → Dominio
    public Product toDomain() {
        Product product = new Product(this.name, this.description);
        product.setId(this.id);

        List<Price> domainPrices = this.prices.stream()
                .map(PriceJpaEntity::toDomain)
                .toList();
        product.setPrices(new ArrayList<>(domainPrices));

        return product;
    }

    // Conversión: Dominio → JPA
    public static ProductJpaEntity fromDomain(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity(domain.getName(), domain.getDescription());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PriceJpaEntity> getPrices() {
        return prices;
    }

    public void setPrices(List<PriceJpaEntity> prices) {
        this.prices = prices;
    }
}

