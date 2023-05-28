package ru.ddc.predictor.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ddc.predictor.entity.Model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class ModelRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ApplicationContext ctx;

    public List<Model> findAll() {

        Resource resource = ctx.getResource("file:volume/models/modelsData.csv");
        try {
            List<Map<String, Object>> mapList = jdbcTemplate.queryForList(
                    String.format(
                            "SELECT * FROM \"%s\";",
                            resource.getFile()
                    )
            );
            List<Model> models = new ArrayList<>();
            for (Map<String, Object> map : mapList) {
                Model model = new Model();
                model.setName((String) map.get("column0"));
                model.setDescription((String) map.get("column1"));
                model.setFilepath((String) map.get("column2"));
                models.add(model);
            }
            return models;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
