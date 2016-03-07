package com.jabuedata.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

public class V1_0_3__UsersImgrationPortraits implements SpringJdbcMigration {

    JdbcTemplate jdbcTemplate;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        alterTable();
        insertAdminPortrait();
    }

    void alterTable() {
        jdbcTemplate.execute(
                "ALTER TABLE users" +
                        "  ADD COLUMN portrait_image_id BIGINT, " +
                        "  ADD CONSTRAINT fk_users_portrait_id FOREIGN KEY (portrait_image_id) REFERENCES images(id)"
        );
    }

    void insertAdminPortrait() {
        jdbcTemplate.update(
                "INSERT INTO images(path, title, width, height, date_updated) " +
                        "VALUES (?, ?, ?, ?, ?)",
                new Object[]{
                        "/assets/ppl-girl-1-56x56.png",
                        "admin girl",
                        56,
                        56,
                        new Date()
                });

        Long picId = jdbcTemplate.queryForObject(
                "SELECT id FROM images WHERE path = '/assets/ppl-girl-1-56x56.png'", Long.class);

        jdbcTemplate.update(
                "UPDATE users SET portrait_image_id = ? WHERE username = 'admin'",
                new Object[]{picId});
    }
}
