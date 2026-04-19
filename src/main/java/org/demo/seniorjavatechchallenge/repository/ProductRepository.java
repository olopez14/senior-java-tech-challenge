package org.demo.seniorjavatechchallenge.repository;

import org.demo.seniorjavatechchallenge.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

