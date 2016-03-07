package com.jabuedata.migration;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;

public class V1_0_1__UserGroupAuthorityMigration implements SpringJdbcMigration {

    JdbcTemplate jdbcTemplate;

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        this.jdbcTemplate = jdbcTemplate;
        createTables();
        addForeignKeys();
        insertGroups();
        insertAuthorities();
        insertGroupAuthorities();
        insertRootUser();
        insertHostUser();
        insertGuestUser();
    }

    void createTables() {
        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                        "  id                   BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  username             VARCHAR(64) NOT NULL UNIQUE," +
                        "  creation_method      VARCHAR(32) NOT NULL, " +
                        "  email                VARCHAR(128)," +
                        "  password             VARCHAR(128)," +
                        "  auto_password        BOOL," +
                        "  first_name           VARCHAR(32)," +
                        "  last_name            VARCHAR(32)," +
                        "  gender               VARCHAR(32)," +
                        "  telephone            VARCHAR(64)," +
                        "  occupation           VARCHAR(64)," +
                        "  introduction         LONGTEXT," +
                        "  date_created         DATETIME NOT NULL," +
                        "  date_last_login      DATETIME," +
                        "  last_updated         DATETIME," +
                        "  enabled              BOOL," +
                        "  account_expired      BOOL," +
                        "  account_locked       BOOL," +
                        "  password_expired     BOOL," +
                        "  facebook_id          VARCHAR(128)," +
                        "  wechat_id            VARCHAR(128)," +
                        "  email_verified       BOOL," +
                        "  telephone_verified   BOOL," +
                        "  id_verified          BOOL," +
                        "  pwd_reset_date       DATETIME," +
                        "  pwd_reset_hash       DATETIME UNIQUE," +
                        "  version              BIGINT NOT NULL, " +
                        "  group_id             BIGINT NOT NULL" +
                        ") ENGINE = InnoDB"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS groups (" +
                        "  id   BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(64) NOT NULL UNIQUE" +
                        ") ENGINE = InnoDB"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS authorities (" +
                        "  id   BIGINT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(64) NOT NULL UNIQUE" +
                        ") ENGINE = InnoDB"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS groups_authorities (" +
                        "  group_id      BIGINT NOT NULL," +
                        "  authority_id  BIGINT NOT NULL," +
                        "  PRIMARY KEY (group_id, authority_id)" +
                        ") ENGINE = InnoDB"
        );

        jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS users_authorities (" +
                        "  user_id        BIGINT NOT NULL," +
                        "  authority_id   BIGINT NOT NULL," +
                        "  PRIMARY KEY (user_id, authority_id)" +
                        ") ENGINE = InnoDB"
        );
    }

    public void addForeignKeys() {
        jdbcTemplate.update(
                "ALTER TABLE users " +
                        "ADD CONSTRAINT fk_users_group_id FOREIGN KEY (group_id) REFERENCES groups(id)"
        );

        jdbcTemplate.update(
                "ALTER TABLE groups_authorities " +
                        "ADD CONSTRAINT fk_ga_group_id FOREIGN KEY (group_id) REFERENCES groups (id), " +
                        "ADD CONSTRAINT fk_ga_authority_id FOREIGN KEY (authority_id) REFERENCES authorities (id) "
        );

        jdbcTemplate.update(
                "ALTER TABLE users_authorities " +
                        "ADD CONSTRAINT fk_ua_user_id FOREIGN KEY (user_id) REFERENCES users (id)," +
                        "ADD CONSTRAINT fk_ua_authority_id FOREIGN KEY (authority_id) REFERENCES authorities (id) "
        );
    }

    public void insertGroups() {
        jdbcTemplate.update(
                "INSERT INTO groups(name) VALUES " +
                        "('GROUP_ROOT'), ('GROUP_GUEST'), ('GROUP_HOST')"
        );
    }

    public void insertAuthorities() {
        jdbcTemplate.update(
                "INSERT INTO authorities(name) VALUES " +
                        "('ROLE_ROOT'), ('ROLE_GUEST'), ('ROLE_HOST')"
        );
    }

    public void insertGroupAuthorities() {
        jdbcTemplate.update(
                "INSERT INTO groups_authorities (group_id, authority_id)" +
                        "  SELECT" +
                        "    groups.id," +
                        "    authorities.id" +
                        "  FROM groups, authorities" +
                        "  WHERE groups.name = 'GROUP_ROOT' AND authorities.name = 'ROLE_ROOT'"
        );

        jdbcTemplate.update(
                "INSERT INTO groups_authorities (group_id, authority_id)" +
                        "  SELECT" +
                        "  groups.id," +
                        "  authorities.id" +
                        "  FROM groups, authorities" +
                        "  WHERE groups.name = 'GROUP_GUEST' AND authorities.name = 'ROLE_GUEST'"
        );

        jdbcTemplate.update(
                "INSERT INTO groups_authorities (group_id, authority_id)" +
                        "  SELECT" +
                        "    groups.id," +
                        "    authorities.id" +
                        "  FROM groups, authorities" +
                        "  WHERE groups.name = 'GROUP_HOST' AND authorities.name = 'ROLE_HOST'"
        );
    }

    public void insertRootUser() {
        Long rootGroupId = jdbcTemplate.queryForObject(
                "SELECT id FROM groups WHERE name = 'GROUP_ROOT'", Long.class);

        Date now = new Date(1443145775776L);

        // create a root amdin
        jdbcTemplate.update(
                "INSERT INTO users(" +
                        "username, " +
                        "creation_method, " +
                        "email, " +
                        "password, " +
                        "auto_password, " +
                        "first_name, " +
                        "last_name, " +
                        "gender, " +
                        "telephone, " +
                        "occupation, " +
                        "introduction, " +
                        "group_id, " +
                        "version, " +
                        "email_verified, " +
                        "telephone_verified, " +
                        "id_verified, " +
                        "enabled, " +
                        "account_expired, " +
                        "account_locked, " +
                        "password_expired, " +
                        "date_last_login, " +
                        "date_created) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{
                        "admin",
                        "NATIVE",
                        "hong.zhu@ybagapp.com",
                        "$2a$10$b65G59vXvvE4pCr7V2adVO1pk0um7T.n2s7sB2oOCXTZqrUtUyibq", //password: admin123
                        false,
                        "Hong",
                        "Zhu",
                        "MALE",
                        "6047886488",
                        "Computer Programmer",
                        "Yidai No. 1",
                        rootGroupId,
                        0L,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        false,
                        now,
                        now}
        );

        jdbcTemplate.update(
                "INSERT INTO users_authorities (user_id, authority_id)" +
                        "  SELECT" +
                        "    users.id," +
                        "    authorities.id" +
                        "  FROM users, authorities" +
                        "  WHERE users.username = 'admin' AND authorities.name = 'ROLE_ROOT'"
        );
    }

    public void insertHostUser() {
        Long hostGroupId = jdbcTemplate.queryForObject(
                "SELECT id FROM groups WHERE name = 'GROUP_HOST'", Long.class);

        Date now = new Date(1443145775776L);

        // create a root amdin
        jdbcTemplate.update(
                "INSERT INTO users(" +
                        "username, " +
                        "creation_method, " +
                        "email, " +
                        "password, " +
                        "auto_password, " +
                        "first_name, " +
                        "last_name, " +
                        "gender, " +
                        "telephone, " +
                        "occupation, " +
                        "introduction, " +
                        "group_id, " +
                        "version, " +
                        "email_verified, " +
                        "telephone_verified, " +
                        "id_verified, " +
                        "enabled, " +
                        "account_expired, " +
                        "account_locked, " +
                        "password_expired, " +
                        "date_last_login, " +
                        "date_created) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{
                        "host",
                        "NATIVE",
                        "info@ybagapp.com",
                        "$2a$08$VK4KHoEVHPjOp.6VsMb70O2EqA65vR5HuhIDpXX1qElPXd304eWsa", //password: host123
                        false,
                        "host",
                        "Host",
                        "MALE",
                        "3383673365",
                        "Student",
                        "Yidai No. 1",
                        hostGroupId,
                        0L,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        false,
                        now,
                        now}
        );

        jdbcTemplate.update(
                "INSERT INTO users_authorities (user_id, authority_id)" +
                        "  SELECT" +
                        "    users.id," +
                        "    authorities.id" +
                        "  FROM users, authorities" +
                        "  WHERE users.username = 'host' AND authorities.name = 'ROLE_HOST'"
        );
    }

    public void insertGuestUser() {
        Long guestGroupId = jdbcTemplate.queryForObject(
                "SELECT id FROM groups WHERE name = 'GROUP_GUEST'", Long.class);

        Date now = new Date(1443145775776L);

        // create a root amdin
        jdbcTemplate.update(
                "INSERT INTO users(" +
                        "username, " +
                        "creation_method, " +
                        "email, " +
                        "password, " +
                        "auto_password, " +
                        "first_name, " +
                        "last_name, " +
                        "gender, " +
                        "telephone, " +
                        "occupation, " +
                        "introduction, " +
                        "group_id, " +
                        "version, " +
                        "email_verified, " +
                        "telephone_verified, " +
                        "id_verified, " +
                        "enabled, " +
                        "account_expired, " +
                        "account_locked, " +
                        "password_expired, " +
                        "date_last_login, " +
                        "date_created) " +
                        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                new Object[]{
                        "guest",
                        "NATIVE",
                        "info@ybagapp.com",
                        "$2a$08$xD05fRathu10WWigRXHBbeUmMc7wWtyI8FhcReUUNeBXVx/XEMXNO", //password: guest123
                        false,
                        "guest",
                        "Guest",
                        "FEMALE",
                        "3389873365",
                        "Student",
                        "Yidai No. 1",
                        guestGroupId,
                        0L,
                        true,
                        true,
                        true,
                        true,
                        false,
                        false,
                        false,
                        now,
                        now}
        );

        jdbcTemplate.update(
                "INSERT INTO users_authorities (user_id, authority_id)" +
                        "  SELECT" +
                        "    users.id," +
                        "    authorities.id" +
                        "  FROM users, authorities" +
                        "  WHERE users.username = 'guest' AND authorities.name = 'ROLE_GUEST'"
        );
    }
}