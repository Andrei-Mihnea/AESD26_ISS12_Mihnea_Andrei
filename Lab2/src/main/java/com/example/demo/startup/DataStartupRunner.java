package com.example.demo.startup;

import com.example.demo.data.LoadedData;
import com.example.demo.reader.DataFileReader;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataStartupRunner implements CommandLineRunner {
    private final DataFileReader dataFileReader;
    private final LoadedData loadedData;

    public DataStartupRunner(DataFileReader dataFileReader,
                             LoadedData loadedData) {
        this.dataFileReader = dataFileReader;
        this.loadedData = loadedData;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        List<String> records = dataFileReader.readRecords();
        loadedData.setRecords(records);

        System.out.println("Loaded " + records.size() + " records:");
        records.stream()
                .limit(3)
                .forEach(System.out::println);
    }
}