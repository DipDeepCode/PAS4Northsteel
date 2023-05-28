package ru.ddc.duckTimeTest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@EnableScheduling
@SpringBootApplication
public class DuckTimeTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(DuckTimeTestApplication.class, args);
	}

//	@Bean
//	public JdbcTemplate jdbcTemplate(DataSource dataSource){
//		JdbcTemplate duckdb =new JdbcTemplate();
//		duckdb.setDataSource(dataSource);
//		return duckdb;
//	}
//	@Bean
//	public DataSource dataSource() {
//		HikariConfig config = new HikariConfig();
//		config.setDriverClassName("org.duckdb.DuckDBDriver");
//		config.setMaximumPoolSize(10);
//		config.setMaxLifetime(3);
//		config.setJdbcUrl("jdbc:duckdb:");
//		return new HikariDataSource(config);
//	}
}
