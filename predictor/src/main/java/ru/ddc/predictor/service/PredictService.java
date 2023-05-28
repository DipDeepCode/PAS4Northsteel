package ru.ddc.predictor.service;

import java.util.Map;

public interface PredictService {

    double predict(org.pmml4s.model.Model model, Map<String, Double> values);
}
