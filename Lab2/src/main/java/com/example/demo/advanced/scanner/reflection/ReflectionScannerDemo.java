package com.example.demo.advanced.scanner.reflection;

import java.nio.file.Path;

public class ReflectionScannerDemo {
    public static void main(String[] args) throws Exception {
        Path classesDirectory = Path.of("component-scan-experiment/classes");

        ReflectionComponentScanner scanner = new ReflectionComponentScanner();
        var components = scanner.scan(classesDirectory, 1000);

        System.out.println("Found " + components.size() + " components");
        components.forEach(System.out::println);
    }
}