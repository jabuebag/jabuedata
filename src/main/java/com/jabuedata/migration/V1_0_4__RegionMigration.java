package com.jabuedata.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_0_4__RegionMigration implements SpringJdbcMigration {

    JdbcTemplate jdbcTemplate;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        createTable();
        insertRegion();
    }

    void createTable() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS regions (" +
                        "  id                       BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  name                     VARCHAR(128) NOT NULL UNIQUE," +
                        "  english_name             VARCHAR(128) NOT NULL UNIQUE," +
                        "  display_order            SMALLINT" +
                        ")"
        );
    }

    public void insertRegion() {
        jdbcTemplate.update("INSERT INTO regions(id, name, english_name, display_order) VALUES (1, '大温哥华地区', 'Greater Vancouver Area', 1)");
        jdbcTemplate.update("INSERT INTO regions(id, name, english_name, display_order) VALUES (2, '维多利亚地区', 'Victoria Area', 2)");
        jdbcTemplate.update("INSERT INTO regions(id, name, english_name, display_order) VALUES (3, '大多伦多地区', 'Greater Toronto Area', 3)");
        jdbcTemplate.update("INSERT INTO regions(id, name, english_name, display_order) VALUES (4, '大西雅图地区', 'Greater Seattle Area', 4)");
    }

}
