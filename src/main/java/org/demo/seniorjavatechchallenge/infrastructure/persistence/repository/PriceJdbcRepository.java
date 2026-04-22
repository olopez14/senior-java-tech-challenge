package org.demo.seniorjavatechchallenge.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.demo.seniorjavatechchallenge.infrastructure.persistence.entity.PriceEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class PriceJdbcRepository {

    private static final Logger log = LoggerFactory.getLogger(PriceJdbcRepository.class);
    private final JdbcTemplate jdbcTemplate;

    public PriceJdbcRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<PriceEntity> PRICE_MAPPER = (rs, rowNum) -> {
        PriceEntity p = new PriceEntity();
        long id = rs.getLong("id");
        if (!rs.wasNull()) p.setId(id);
        p.setProductId(rs.getLong("product_id"));
        p.setPriceValue(rs.getBigDecimal("price_value"));
        p.setInitDate(rs.getObject("init_date", LocalDate.class));
        p.setEndDate(rs.getObject("end_date", LocalDate.class));
        return p;
    };

    public List<PriceEntity> findByProductIdOrderByInitDateAsc(Long productId) {
        String sql = "SELECT id, product_id, price_value, init_date, end_date FROM prices WHERE product_id = ? ORDER BY init_date ASC";
        return jdbcTemplate.query(sql, PRICE_MAPPER, productId);
    }

    public Optional<PriceEntity> findCurrentPrice(Long productId, LocalDate date) {
        String sql = "SELECT id, product_id, price_value, init_date, end_date FROM prices "
                + "WHERE product_id = ? AND init_date <= ? AND (end_date IS NULL OR end_date >= ?) "
                + "ORDER BY init_date DESC LIMIT 1";
        var list = jdbcTemplate.query(sql, PRICE_MAPPER, productId, date, date);
        return list.stream().findFirst();
    }

    public Optional<BigDecimal> findCurrentPriceValue(Long productId, LocalDate date) {
        String sql = "SELECT price_value FROM prices "
                + "WHERE product_id = ? AND init_date <= ? AND (end_date IS NULL OR end_date >= ?) "
                + "ORDER BY init_date DESC LIMIT 1";
        var list = jdbcTemplate.queryForList(sql, BigDecimal.class, productId, date, date);
        return list.stream().findFirst();
    }

    public boolean existsOverlappingPrice(Long productId, LocalDate initDate, LocalDate endDate) {
        LocalDate effectiveEnd = endDate != null ? endDate : initDate;
        String sql = "SELECT COUNT(*) FROM prices WHERE product_id = ? AND init_date <= ? AND (end_date IS NULL OR end_date >= ?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productId, effectiveEnd, initDate);
        return count != null && count > 0;
    }

    public List<PriceEntity> findOverlappingPrices(Long productId, LocalDate initDate, LocalDate endDate) {
        LocalDate effectiveEnd = endDate != null ? endDate : initDate;
        String sql = "SELECT id, product_id, price_value, init_date, end_date FROM prices "
                + "WHERE product_id = ? AND init_date <= ? AND (end_date IS NULL OR end_date >= ?)";
        return jdbcTemplate.query(sql, PRICE_MAPPER, productId, effectiveEnd, initDate);
    }

    public PriceEntity save(PriceEntity price) {
        if (price.getProductId() == null) {
            throw new IllegalArgumentException("Product id is required");
        }

        if (price.getId() == null) {
            String insert = "INSERT INTO prices (product_id, price_value, init_date, end_date) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, price.getProductId());
                ps.setBigDecimal(2, price.getPriceValue());
                ps.setObject(3, price.getInitDate());
                ps.setObject(4, price.getEndDate());
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key != null) price.setId(key.longValue());
            return price;
        } else {
            String update = "UPDATE prices SET product_id = ?, price_value = ?, init_date = ?, end_date = ? WHERE id = ?";
            jdbcTemplate.update(update, price.getProductId(), price.getPriceValue(), price.getInitDate(), price.getEndDate(), price.getId());
            return price;
        }
    }
}
