package com.example.demo.actuator;

import com.example.demo.config.DataFileProperties;
import com.example.demo.data.LoadedData;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataFileInfoContributor implements InfoContributor {
    private final DataFileProperties properties;
    private final LoadedData loadedData;

    public DataFileInfoContributor(
            DataFileProperties properties,
            LoadedData loadedData) {
        this.properties = properties;
        this.loadedData = loadedData;
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("dataSource", Map.of(
                "file", properties.getFile(),
                "format", properties.getFormat(),
                "recordCount", loadedData.getRecordCount()
        ));
    }
}