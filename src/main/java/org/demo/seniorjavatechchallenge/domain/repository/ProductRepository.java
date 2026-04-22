package org.demo.seniorjavatechchallenge.domain.repository;

import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.model.Product;


public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findByIdForUpdate(Long id);
}





