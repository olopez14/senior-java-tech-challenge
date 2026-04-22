package org.demo.seniorjavatechchallenge.infrastructure.persistence.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.ProductEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class ProductJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<ProductEntity> PRODUCT_MAPPER = (rs, rowNum) -> {
        ProductEntity p = new ProductEntity();
        long id = rs.getLong("id");
        if (!rs.wasNull()) p.setId(id);
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        return p;
    };

    public ProductEntity save(ProductEntity product) {
        if (product.getId() == null) {
            String insert = "INSERT INTO products (name, description) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, product.getName());
                ps.setString(2, product.getDescription());
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key != null) product.setId(key.longValue());
            return product;
        } else {
            String update = "UPDATE products SET name = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(update, product.getName(), product.getDescription(), product.getId());
            return product;
        }
    }

    public Optional<ProductEntity> findById(Long id) {
        String sql = "SELECT id, name, description FROM products WHERE id = ?";
        var list = jdbcTemplate.query(sql, PRODUCT_MAPPER, id);
        return list.stream().findFirst();
    }

    public Optional<ProductEntity> findByIdForUpdate(Long id) {
        String sql = "SELECT id, name, description FROM products WHERE id = ? FOR UPDATE";
        var list = jdbcTemplate.query(sql, PRODUCT_MAPPER, id);
        return list.stream().findFirst();
    }
}

