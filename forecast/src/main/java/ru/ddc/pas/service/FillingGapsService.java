package ru.ddc.pas.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ddc.pas.persistence.dao.TestInterval;
import ru.ddc.pas.persistence.repository.TestIntervalRepository;
import ru.ddc.pas.persistence.repository.XTestRepository;
import ru.ddc.pas.utils.BestWMFCalculator;
import ru.ddc.pas.utils.CSVParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FillingGapsService {
    private final XTestRepository xTestRepository;
    private final DateTimeFormatter formatter;
    private final TestIntervalRepository testIntervalsRepository;
    private final Logger log = LoggerFactory.getLogger(FillingGapsService.class);
    private final List<Object[]> cachedResultsFromBestWmfMiddle = new ArrayList<>();

    @Value("${wmf.filename}")
    private String wmfFilename;

    public void predict() {
        Map<String, List<Float>> wmf_np = CSVParser.parseAllColumns(wmfFilename);
        int periodLengthInPoints = 3000;
        int pointDurationInSeconds = 10;
        int step = 30;
        int point_price = 3000 / step;

        List<TestInterval> testIntervalList = testIntervalsRepository.getAll();
        List<String> columnNames = xTestRepository.getColumnNames();
        columnNames = columnNames.subList(1, columnNames.size());

        for (int ti = 185; ti < testIntervalList.size(); ti++) {//TestInterval testInterval : testIntervalList
            TestInterval testInterval = testIntervalList.get(ti);
            LocalDateTime gapStartDateTime = testInterval.getStart();
            LocalDateTime gapEndDate = testInterval.getFinish();
            log.info("Период пропущенных значений: {} - {}",
                    gapStartDateTime.format(formatter),
                    gapEndDate.format(formatter));
            for (int ci = (ti == 185 ? 13 : 0); ci < columnNames.size(); ci++) {//String columnName : columnNames
                String columnName = columnNames.get(ci);
                if (columnName.contains("ЭКСГАУСТЕР 4")) {
                    int shiftInPoints = 0;
                    int maxShiftInPoints = 360;

                    List<Object[]> objects = new ArrayList<>();
                    do {
                        LocalDateTime periodEndDateTime = gapStartDateTime.minusSeconds((long) shiftInPoints * pointDurationInSeconds);
                        LocalDateTime periodStartDateTime = periodEndDateTime.minusSeconds(periodLengthInPoints * pointDurationInSeconds).minusSeconds((long) shiftInPoints * pointDurationInSeconds);
                        log.info("Используем данные из столбца: {} с {} по {}", columnName, periodStartDateTime.format(formatter), periodEndDateTime.format(formatter));
                        List<Float> df_1interv_1opora = xTestRepository.getValues(columnName, periodStartDateTime, periodEndDateTime);

                        long numberOfNotNullValues = df_1interv_1opora.stream()
                                .filter(Objects::nonNull)
                                .count();

                        if (numberOfNotNullValues > 1) {

                            List<Float> maxi_vibr = new ArrayList<>();
                            List<Float> mini_vibr = new ArrayList<>();
                            List<Float> midi_vibr = new ArrayList<>();


                            for (int i = 0; i < df_1interv_1opora.size() - 1; i += step) {
                                List<Float> df2 = df_1interv_1opora.subList(i, i + step);
                                //                            System.out.println(df2);
                                maxi_vibr.add(
                                        (float) df2.stream()
                                                .filter(Objects::nonNull)
                                                .mapToDouble(value -> (double) value)
                                                .max()
                                                .orElse(0.0)
                                );
                                mini_vibr.add(
                                        (float) df2.stream()
                                                .filter(Objects::nonNull)
                                                .mapToDouble(value -> (double) value)
                                                .min()
                                                .orElse(0.0)
                                );
                                midi_vibr.add(
                                        (float) df2.stream()
                                                .filter(Objects::nonNull)
                                                .mapToDouble(value -> (double) value)
                                                .average()
                                                .orElse(0.0)
                                );
                            }

                            objects = predict_1(midi_vibr, maxi_vibr, mini_vibr, wmf_np, point_price, shiftInPoints);
                        }
                        if (objects.size() > 0) {
                            log.info("Количество совпадений в предсказании: {}", objects.size());
                        } else {
                            shiftInPoints += 60;
                            if (shiftInPoints <= maxShiftInPoints) {
                                log.info("Совпадений в предсказании нет. Производится сдвиг на {} минут назад",
                                        shiftInPoints * pointDurationInSeconds / 60);
                            } else {
                                log.info("Совпадений не найдено. Для расчетов будет использован весь best_wmf_middle");
                                objects = cachedResultsFromBestWmfMiddle;
                            }
                        }
                    } while (shiftInPoints <= maxShiftInPoints && objects.size() == 0);

                    if (objects.size() > 0) {
                        Object[] dataset_identification = objects
                                .stream()
                                .min(Comparator.comparingDouble(value -> (float) value[1]))
                                .orElse(new Object[]{});
                        log.info("Выбран ряд с минимальным cmo");


                        List<Float> result_list = line_predict(0, dataset_identification, wmf_np);
                        log.info("Вычислено предсказание: {}, {}, {}, {}, {}...",
                                result_list.get(0),
                                result_list.get(1),
                                result_list.get(2),
                                result_list.get(3),
                                result_list.get(4));

                        LocalDateTime podGapStartDateTime = gapStartDateTime;
                        LocalDateTime podGapEndDateTime = podGapStartDateTime.plusMinutes(5);
                        if (gapEndDate.isBefore(podGapEndDateTime)) {
                            podGapEndDateTime = gapEndDate;
                        }
                        for (Float value : result_list) {
                            xTestRepository.updateValues(columnName, podGapStartDateTime, podGapEndDateTime, value);
                            podGapStartDateTime = podGapStartDateTime.plusMinutes(5);
                            podGapEndDateTime = podGapEndDateTime.plusMinutes(5);
                            if (gapEndDate.isBefore(podGapStartDateTime)) {
                                break;
                            }
                            if (gapEndDate.isBefore(podGapEndDateTime)) {
                                podGapEndDateTime = gapEndDate;
                            }
                        }
                    } else {
                        log.info("best_wmf_middle тоже пустой. Этот участок пропускается");
                    }
                    String filename = ti + "_" + ci + "_" + columnName;
                    xTestRepository.dump(filename);
                }
            }
        }
        xTestRepository.dump();
        log.info("Выполнено сохранение из in-memory базы данных в файл");
    }

    private List<Object[]> predict_1(List<Float> price, List<Float> priceHigh, List<Float> priceLow, Map<String, List<Float>> wmf, int point_price, int shiftPoints) {
        List<Object[]> result_list = new ArrayList<>();

        for (int m = 20; m < 105; m += 5) {

            BestWMFCalculator best_wmf_middle_object = new BestWMFCalculator(price, wmf, point_price, m);
            BestWMFCalculator best_wmf_high_object = new BestWMFCalculator(priceHigh, wmf, point_price, m);
            BestWMFCalculator best_wmf_low_object = new BestWMFCalculator(priceLow, wmf, point_price, m);

            Thread best_wmf_middle_thread = new Thread(best_wmf_middle_object);
            Thread best_wmf_high_thread = new Thread(best_wmf_high_object);
            Thread best_wmf_low_thread = new Thread(best_wmf_low_object);

            best_wmf_middle_thread.start();
            best_wmf_high_thread.start();
            best_wmf_low_thread.start();

            try {
                best_wmf_middle_thread.join();
                best_wmf_high_thread.join();
                best_wmf_low_thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Object[] best_wmf_middle = best_wmf_middle_object.getBest_wmf();
            if (shiftPoints == 0) {
                cachedResultsFromBestWmfMiddle.add(best_wmf_middle);
            }
            Object[] best_wmf_high = best_wmf_high_object.getBest_wmf();
            Object[] best_wmf_low = best_wmf_low_object.getBest_wmf();

            if (best_wmf_low[5] != null && best_wmf_middle[5] != null && best_wmf_high[5] != null &&
                    best_wmf_low[2].equals(best_wmf_middle[2]) && best_wmf_middle[2].equals(best_wmf_high[2]) &&
                    best_wmf_low[5].equals(best_wmf_middle[5]) && best_wmf_middle[5].equals(best_wmf_high[5])) {
                result_list.add(best_wmf_middle);
            }
        }
        return result_list;
    }

    private List<Float> line_predict(int line, Object[] dataset_identificated, Map<String, List<Float>> wmf_full) {
        int m = (int) dataset_identificated[0];
        int i = (int) dataset_identificated[2];
        String l = (String) dataset_identificated[5];
        List<Float> wmf_part = wmf_full.get(l).subList(i, i + m);
        float wmf_max = Collections.max(wmf_part);
        float wmf_min = Collections.min(wmf_part);
        List<Float> x = wmf_full.get(l);
        List<Float> wmf_part_150 = x.subList(i, Math.min(i + 150, x.size()));
        List<Float> wmf_part_norm = normalize1(wmf_part_150, wmf_max, wmf_min);
        float min = (float) dataset_identificated[3];
        float max = (float) dataset_identificated[4];
        return denormalize(wmf_part_norm, max, min);
    }

    private List<Float> normalize1(List<Float> serie, float max, float min) {
        return serie.stream().map(x -> (x - min) / (max - min)).toList();
    }

    private List<Float> denormalize(List<Float> serie, float max, float min) {
        return serie.stream().map(x -> min + x * (max - min)).toList();
    }
}
