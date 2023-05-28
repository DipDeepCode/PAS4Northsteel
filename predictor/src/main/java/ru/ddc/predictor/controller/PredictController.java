package ru.ddc.predictor.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.ddc.predictor.service.StateService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/predictor")
@RestController
public class PredictController {
    private final StateService stateService;

    @PostMapping("/predict")
    public void predict(@RequestBody Map<String, Object> values) {
        stateService.calculatePredict(values);
    }

    @GetMapping("/getState")
    public List<Map<String, Object>> getState() {
        return stateService.getPredictState();
    }
}
