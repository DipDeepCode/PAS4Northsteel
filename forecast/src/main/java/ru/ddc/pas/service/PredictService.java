package ru.ddc.pas.service;

import org.pmml4s.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ddc.pas.persistence.repository.XTestRepository;

import java.util.Arrays;
import java.util.Map;

@Service
public class PredictService {
//    private final Model model = Model.fromFile("src/main/resources/model-2.pmml");

    public Map<String, Double> predict(Model model, Map<String, Double> values3) {
        Object[] valuesMap = Arrays.stream(model.inputNames())
                .map(values3::get)
                .toArray();
        Object[] result = model.predict(valuesMap);
        return Map.of("Value", (Double) result[0], "Confidence", (Double) result[1]);
    }
}
