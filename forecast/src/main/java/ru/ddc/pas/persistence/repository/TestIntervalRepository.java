package ru.ddc.pas.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ddc.pas.persistence.dao.TestInterval;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class TestIntervalRepository {
    private final JdbcTemplate jdbcTemplate;

    public List<TestInterval> getAll() {
        return jdbcTemplate.query(
                "SELECT start, finish FROM test_intervals",
                (rs, rowNum) -> {
                    LocalDateTime start = rs.getTimestamp("start").toLocalDateTime();
                    LocalDateTime finish = rs.getTimestamp("finish").toLocalDateTime();
                    return new TestInterval(start, finish);
                }
        );
    }
}
