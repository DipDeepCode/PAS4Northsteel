package ru.ddc.pas.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RunAfterStartup {
    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(RunAfterStartup.class);

    @Value("${parquet.filename}")
    private String parquetFilename;

    @Value("${testIntervals.filename}")
    private String testIntervalsFilename;


    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        log.info("Начата загрузка файла {} в in-memory базу данных X_test DuckDB", parquetFilename);
        jdbcTemplate.execute("CREATE TABLE X_test AS SELECT * FROM " + parquetFilename + ";");
        log.info("Файл {} успешно загружен", parquetFilename);

        log.info("Начата загрузка файла {} в in-memory базу данных test_intervals DuckDB", testIntervalsFilename);
        jdbcTemplate.execute("CREATE TABLE test_intervals AS SELECT * FROM " + testIntervalsFilename + ";");
        log.info("Файл {} успешно загружен", testIntervalsFilename);
    }
}
