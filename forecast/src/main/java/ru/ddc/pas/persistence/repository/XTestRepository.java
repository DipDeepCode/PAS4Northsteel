package ru.ddc.pas.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class XTestRepository {
    private final JdbcTemplate jdbcTemplate;
    private final DateTimeFormatter formatter;

    public List<Float> getValues(String columnName, LocalDateTime startDateTimeIncl, LocalDateTime endDateTimeIncl) {
        return jdbcTemplate.queryForList(
                String.format(
                        "SELECT \"%s\" FROM X_test WHERE DT BETWEEN '%s' AND '%s';",
                        columnName,
                        startDateTimeIncl.format(formatter),
                        endDateTimeIncl.format(formatter)
                ),
                Float.class
        );
    }

    public Map<String, Object> getValues2(LocalDateTime dateTime, String... columnNames) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            sb.append("\"").append(columnName).append("\",");
        }
        String columnsForQuery = sb.substring(0, sb.length() - 1);
        return jdbcTemplate.queryForList(
                String.format("SELECT %s FROM X_test WHERE DT = '%s';",
                        columnsForQuery,
                        dateTime.format(formatter)
                )
        ).get(0);
    }

    public List<String> getColumnNames() {
        return jdbcTemplate.queryForList(
                "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name = 'X_test';",
                String.class
        );
    }

    public void updateValues(String columnName, LocalDateTime startDateTimeIncl, LocalDateTime endDateTimeIncl, float value) {
        jdbcTemplate.update(
                String.format(Locale.US,
                        "UPDATE X_test SET \"%s\" = %f WHERE DT BETWEEN '%s' AND '%s';",
                        columnName,
                        value,
                        startDateTimeIncl,
                        endDateTimeIncl
                )
        );
    }

    public void dump() {
        jdbcTemplate.execute("EXPORT DATABASE 'D:\\pas\\dump' (FORMAT PARQUET);");
    }

    public void dump(String filename) {
        jdbcTemplate.execute(String.format(
                "EXPORT DATABASE 'D:\\pas\\dump\\%s' (FORMAT PARQUET);",
                filename)
        );
    }
}
