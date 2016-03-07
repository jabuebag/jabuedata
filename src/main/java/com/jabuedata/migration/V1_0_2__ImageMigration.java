package com.jabuedata.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

public class V1_0_2__ImageMigration implements SpringJdbcMigration {


    JdbcTemplate jdbcTemplate;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        createTables();
    }

    public void createTables() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS images (" +
                        "  id                      BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  path                    VARCHAR(1024)," +
                        "  title                   VARCHAR(128)," +
                        "  width                   INTEGER," +
                        "  height                  INTEGER," +
                        "  date_updated            DATETIME," +
                        "  owner_id                BIGINT" +
                        ")"
        );

        jdbcTemplate.execute(
                "ALTER TABLE images ADD CONSTRAINT fk_images_owner_id FOREIGN KEY (owner_id) REFERENCES users(id)"
        );
    }
}