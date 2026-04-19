package org.demo.seniorjavatechchallenge.repository;

import java.util.Optional;

import org.demo.seniorjavatechchallenge.domain.Product;


public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findByIdForUpdate(Long id);
}




