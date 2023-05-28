package ru.ddc.pas.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pmml4s.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ddc.pas.persistence.repository.XTestRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class PredictServiceTest {
    @Autowired
    private PredictService predictService;
    @Autowired
    private XTestRepository xTestRepository;
    Model model;
    Map<String, Double> values3;

    @BeforeEach
    public void setUp() {
        model = Model.fromFile("src/main/resources/model-2.pmml");
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
        values3 = value2.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, stringObjectEntry -> {
                    Object value = stringObjectEntry.getValue();
                    return value == null ? 0 : (Double) value;
                }));
    }

    @Test
    public void predict1() {
        predictService.predict(model, values3);
    }
}