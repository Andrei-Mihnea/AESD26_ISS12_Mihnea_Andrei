package com.example.demo.reader;

import java.io.IOException;
import java.util.List;

public interface DataFileReader {
    List<String> readRecords() throws IOException;
}