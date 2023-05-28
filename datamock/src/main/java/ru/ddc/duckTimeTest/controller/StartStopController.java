package ru.ddc.duckTimeTest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ddc.duckTimeTest.service.StartStopService;

@RequiredArgsConstructor
@RequestMapping("/datamock")
@RestController
public class StartStopController {
    private final StartStopService initService;

    @PostMapping("/start")
    public void start() {
        initService.start();
    }

    @PostMapping("/stop")
    public void stop() {
        initService.stop();
    }
}
