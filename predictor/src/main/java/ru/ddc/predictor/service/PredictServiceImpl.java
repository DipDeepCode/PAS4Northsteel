package ru.ddc.predictor.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PredictServiceImpl implements PredictService {

    @Override
    public double predict(org.pmml4s.model.Model model, Map<String, Double> values) {

        Map<String, Double> values1 = values.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                stringDoubleEntry -> {
                                    Double val = stringDoubleEntry.getValue();
                                    return (val == null || Double.isNaN(val)) ? 0.0 : val;
                                })
                );

        Object[] valuesMap = Arrays.stream(model.inputNames())
                .map(values1::get)
                .toArray();
        Object[] result = model.predict(valuesMap);
        return (double) result[0];
    }
}
