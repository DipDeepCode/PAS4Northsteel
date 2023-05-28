package ru.ddc.pas.service;

import lombok.RequiredArgsConstructor;
import org.pmml4s.model.Model;
import org.springframework.stereotype.Service;
import ru.ddc.pas.persistence.repository.XTestRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PreparePredictService {
    private final Model model = Model.fromFile("src/main/resources/model-2.pmml");
    private final XTestRepository xTestRepository;
    private final DateTimeFormatter formatter;

    public void predict_tt() {
        Map<String, Object> value2 = xTestRepository.getValues2(
                LocalDateTime.parse("2022-01-01T00:00:00"),
                "DT",
                "ЭКСГАУСТЕР 4. ТОК РОТОРА 1",
                "ЭКСГАУСТЕР 4. ТОК РОТОРА2",
                "ЭКСГАУСТЕР 4. ТОК СТАТОРА",
                "ЭКСГАУСТЕР 4. ДАВЛЕНИЕ МАСЛА В СИСТЕМЕ",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА ПОДШИПНИКА НА ОПОРЕ 1",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА ПОДШИПНИКА НА ОПОРЕ 2",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА ПОДШИПНИКА НА ОПОРЕ 3",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА ПОДШИПНИКА НА ОПОРЕ 4",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА МАСЛА В СИСТЕМЕ",
                "ЭКСГАУСТЕР 4. ТЕМПЕРАТУРА МАСЛА В МАСЛОБЛОКЕ",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 1",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 2",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 3",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 3. ПРОДОЛЬНАЯ.",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 4",
                "ЭКСГАУСТЕР 4. ВИБРАЦИЯ НА ОПОРЕ 4. ПРОДОЛЬНАЯ.");

        Object dateTimeMap = value2.get("DT");
        System.out.print(dateTimeMap + " - ");
        value2.remove("DT");

        Map<String, Double> values3 = value2.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, stringObjectEntry -> {
                    Object value = stringObjectEntry.getValue();
                    return value == null ? 0 : (Double) value;
                }));
        Object[] valuesMap = Arrays.stream(model.inputNames())
                .map(values3::get)
                .toArray();
        Object[] result = model.predict(valuesMap);
        System.out.println(Arrays.toString(result));
/*        Map<String, Double> value3 = values3.get(0);
        for (String key : value3.keySet()) {
            System.out.println(key + " -> " + value3.get(key));
        }
        Map<String, Double> value4 = values3.get(1);
        for (String key : value4.keySet()) {
            System.out.println(key + " -> " + value4.get(key));
        }*/
    }
}
