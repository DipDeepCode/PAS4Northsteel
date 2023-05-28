package ru.ddc.duckTimeTest.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ddc.duckTimeTest.repository.XTestRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@RequiredArgsConstructor
@EnableScheduling
@Service
public class ScheduledService {
    private AtomicBoolean enabled = new AtomicBoolean(false);
    private final XTestRepository repository;
    private final RequestService requestService;

    @Value("#{T(java.time.LocalDateTime).parse('${startdatetime}')}")
    private LocalDateTime dateTime;

    @Scheduled(fixedDelay = 1000 * 10)
    public void scheduleTask() {
        if (enabled.get()) {
            Map<String, Object> values = repository.getRow(dateTime);
            requestService.doRequest(values);
            dateTime = dateTime.plusSeconds(10);
        }
    }
}
