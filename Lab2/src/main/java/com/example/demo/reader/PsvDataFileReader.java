package com.example.demo.reader;

import com.example.demo.config.DataFileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Profile("prod")
@ConditionalOnExpression("'${app.data.format:}' == 'psv'")
public class PsvDataFileReader implements DataFileReader {
    private final DataFileProperties properties;
    private final ResourceLoader resourceLoader;

    @Autowired
    public PsvDataFileReader(
            DataFileProperties properties,
            ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public List<String> readRecords() throws IOException {
        Resource resource = resourceLoader.getResource(properties.getFile());

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1) // Skip the header row
                    .toList();
        }
    }
}