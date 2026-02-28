package com.backend.allreva.module.concert.place.infra.kopis;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CsvUtil {

    public static List<String> readConcertHallCodes() {
        ClassLoader classLoader = CsvUtil.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("kopis/performance_hall_list.csv"))
                .getFile());
        List<String> ids = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            String[] line;
            csvReader.readNext();

            while ((line = csvReader.readNext()) != null) {
                String id = line[0];
                ids.add(id);
            }
        } catch (Exception e) {
            log.error("can't fetch hall ids.");
            log.error("Error Message: {}", e.getMessage());
        }

        return ids;
    }
}
