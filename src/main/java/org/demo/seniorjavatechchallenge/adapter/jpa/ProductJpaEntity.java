package org.demo.seniorjavatechchallenge.adapter.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.demo.seniorjavatechchallenge.domain.Product;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "products")
@Getter
@Setter
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

    
    public Product toDomain() {
        Product product = new Product(this.name, this.description);
        product.setId(this.id);
        product.setPrices(new ArrayList<>()); 
        return product;
    }

    
    public static ProductJpaEntity fromDomain(Product domain) {
        ProductJpaEntity entity = new ProductJpaEntity(domain.getName(), domain.getDescription());
        entity.id = domain.getId();
        return entity;
    }

}



