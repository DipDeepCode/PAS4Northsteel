package ru.ddc.duckTimeTest.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class XTestRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter;
    private final ApplicationContext ctx;

    public Map<String, Object> getRow(LocalDateTime dateTime) {

        Resource resource = ctx.getResource("file:volume/x_test.parquet");
        try {
            return jdbcTemplate.queryForMap(
                    String.format(
                            "SELECT * FROM \"%s\" WHERE DT = '%s';",
                            resource.getFile(),
                            dateTime.format(formatter)
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
