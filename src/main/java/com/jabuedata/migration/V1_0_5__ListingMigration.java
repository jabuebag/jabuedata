package com.jabuedata.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_0_5__ListingMigration implements SpringJdbcMigration {

    JdbcTemplate jdbcTemplate;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        createTable();
        addForeignKey();
    }

    void createTable() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS listings (" +
                        "  id                       BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  title                    VARCHAR(128)," +
                        "  introduction             VARCHAR(255)," +
                        "  description              LONGTEXT," +
                        "  assemble_point           VARCHAR(32)," +
                        "  price                    DOUBLE," +
                        "  status                   VARCHAR(32)," +
                        "  quantity                 INTEGER," +
                        "  dine_service             BIGINT," +
                        "  beverage_service         BIGINT," +
                        "  clean_service            BIGINT," +
                        "  date_created             DATETIME," +
                        "  date_updated             DATETIME," +
                        "  cover_image_id           BIGINT," +
                        "  cover_image_long_id      BIGINT," +
                        "  owner_id                 BIGINT," +
                        "  version                  BIGINT," +
                        "  region_id                BIGINT" +
                        ")"
        );
    }

    public void addForeignKey() {
        jdbcTemplate.update(
                "ALTER TABLE listings " +
                        "ADD CONSTRAINT fk_listings_cover_image_id FOREIGN KEY (cover_image_id) REFERENCES images (id), " +
                        "ADD CONSTRAINT fk_listings_cover_image_long_id FOREIGN KEY (cover_image_long_id) REFERENCES images (id), " +
                        "ADD CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (id), " +
                        "ADD CONSTRAINT fk_region_id FOREIGN KEY (region_id) REFERENCES regions (id) "

        );
    }

}
