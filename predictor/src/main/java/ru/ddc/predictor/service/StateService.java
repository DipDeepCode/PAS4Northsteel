package ru.ddc.predictor.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ddc.predictor.entity.Model;
import ru.ddc.predictor.repository.ModelRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class StateService {
    private final ModelRepository modelRepository;
    private final PredictService predictService;
    private final Map<Model, Malfunction> predictState = new HashMap<>();
    private Map<String, Object> inputValuesState;
    private List<Model> allRegisteredModels;

    public void calculatePredict(Map<String, Object> allValues) {
        inputValuesState = allValues;
        Map<String, Double> values = getDoubleValuesWithoutDateTime(allValues);

        if (allRegisteredModels == null) {
            allRegisteredModels = modelRepository.findAll();
        }

        Map<Model, Integer> predictResult = calculatePredictForAllModels(allRegisteredModels, values);
        extracted(predictResult);
    }

    private void extracted(Map<Model, Integer> predictResult) {
        for (Map.Entry<Model, Integer> entry : predictResult.entrySet()) {
            int newStatus = entry.getValue();
            if (predictState.containsKey(entry.getKey())) {
                int currentStatus = predictState.get(entry.getKey()).getIndex();
                int futureStatus;
                if (currentStatus == 0) {
                    futureStatus = newStatus;
                } else if (currentStatus == 1) {
                    futureStatus = 1;
                } else if (currentStatus == 2 && newStatus == 1) {
                    futureStatus = 1;
                } else {
                    futureStatus = 2;
                }
                if (futureStatus != newStatus) {
                    predictState.replace(entry.getKey(), new Malfunction(5, futureStatus));
                }
            } else {
                if (newStatus == 0) {
                    predictState.put(entry.getKey(), new Malfunction(newStatus));
                } else {
                    predictState.put(entry.getKey(), new Malfunction(5, entry.getValue()));
                }
            }
        }
    }

    private Map<String, Double> getDoubleValuesWithoutDateTime(Map<String, Object> allValues) {
        return allValues.entrySet().stream()
                .skip(1)
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                stringObjectEntry -> stringObjectEntry.getValue() == null ? 0.0 : (Double) stringObjectEntry.getValue()
                        )
                );
    }

    public List<Map<String, Object>> getPredictState() {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> objectState = predictState.entrySet().stream()
                        .collect(Collectors.toMap(key -> key.getKey().getDescription(), e -> e.getValue().getIndex()));
        result.add(objectState);
        result.add(inputValuesState);
        return result;
    }

    private Map<Model, Integer> calculatePredictForAllModels(List<Model> all, Map<String, Double> values) {
        Map<Model, Integer> resultMap = new HashMap<>();
        for (Model model : all) {
            String filepath = model.getFilepath();
            org.pmml4s.model.Model model1 = org.pmml4s.model.Model.fromFile(filepath);
            double predict = predictService.predict(model1, values);
            resultMap.put(model, (int) predict);
        }
        return resultMap;
    }
}
