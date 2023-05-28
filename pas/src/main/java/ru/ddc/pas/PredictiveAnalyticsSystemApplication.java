package ru.ddc.pas;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class PredictiveAnalyticsSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PredictiveAnalyticsSystemApplication.class, args);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate duckDB = new JdbcTemplate();
        duckDB.setDataSource(dataSource);
        return duckDB;
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.duckdb.DuckDBDriver");
        config.setMaximumPoolSize(10);
        config.setMaxLifetime(3);
        config.setJdbcUrl("jdbc:duckdb:");
        return new HikariDataSource(config);
    }
}
