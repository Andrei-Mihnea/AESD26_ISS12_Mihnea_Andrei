package com.example.demo.advanced.scanner.asm;

import java.nio.file.Path;

public class AsmScannerDemo {
    public static void main(String[] args) throws Exception {
        var scanner = new AsmComponentScanner();
        var components = scanner.scan(Path.of("component-scan-experiment/classes/generated"));

        System.out.println("Found " + components.size() + " components");
        components.forEach(System.out::println);
    }
}
