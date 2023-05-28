package ru.ddc.duckTimeTest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ddc.duckTimeTest.repository.XTestRepository;

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class StartStopService {
    private final XTestRepository repository;
    private final ScheduledService scheduledService;

    public void start() {
        scheduledService.setEnabled(new AtomicBoolean(true));
    }

    public void stop() {
        scheduledService.setEnabled(new AtomicBoolean(false));
    }
}
