package com.example.demo.data;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadedData {
    private List<String> records = List.of();

    public void setRecords(List<String> records) {
        this.records = records;
    }

    public int getRecordCount() {
        return records.size();
    }
}