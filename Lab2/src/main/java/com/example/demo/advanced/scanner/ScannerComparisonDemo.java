package com.example.demo.advanced.scanner;

import com.example.demo.advanced.scanner.asm.AsmComponentScanner;
import com.example.demo.advanced.scanner.reflection.ReflectionComponentScanner;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class ScannerComparisonDemo {
    private static final int CLASS_COUNT = 1000;
    private static final int WARMUP_RUNS = 5;
    private static final int MEASURED_RUNS = 20;

    public static void main(String[] args) throws Exception {
        Path classesDirectory = Path.of("component-scan-experiment/classes");

        var reflectionScanner = new ReflectionComponentScanner();
        var asmScanner = new AsmComponentScanner();

        if (args.length > 0 && args[0].equalsIgnoreCase("reflection")) {
            System.out.println("Reflection found: "
                    + reflectionScanner.scan(classesDirectory, CLASS_COUNT).size());
            return;
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("asm")) {
            System.out.println("ASM found: "
                    + asmScanner.scan(classesDirectory.resolve("generated")).size());
            return;
        }

        Timing reflectionTiming = measure(
                () -> reflectionScanner.scan(classesDirectory, CLASS_COUNT));
        Timing asmTiming = measure(
                () -> asmScanner.scan(classesDirectory.resolve("generated")));

        Set<String> reflectionSet = new HashSet<>(
                reflectionScanner.scan(classesDirectory, CLASS_COUNT));
        Set<String> asmSet = new HashSet<>(
                asmScanner.scan(classesDirectory.resolve("generated")));

        System.out.println("Reflection vs ASM component scanning");
        System.out.printf("Dataset: %d classes; warm-ups: %d; measured runs: %d%n",
                CLASS_COUNT, WARMUP_RUNS, MEASURED_RUNS);
        System.out.println("+----------------+------------------+------------------------+----------------+------------------------------+");
        System.out.println("| Approach       | First scan (ms)  | Warm runs avg ± SD (ms)| Components     | Loading / initialization     |");
        System.out.println("+----------------+------------------+------------------------+----------------+------------------------------+");
        printRow("Reflection", reflectionTiming, reflectionSet.size(),
                "loads classes; no static init");
        printRow("ASM", asmTiming, asmSet.size(), "reads bytecode; no class load");
        System.out.println("+----------------+------------------+------------------------+----------------+------------------------------+");
        System.out.println("Same discovered classes: " + reflectionSet.equals(asmSet));
        System.out.println("Missing dependency case: run MissingDependencyDemo");

        if (!reflectionSet.equals(asmSet)) {
            Set<String> onlyInReflection = new HashSet<>(reflectionSet);
            onlyInReflection.removeAll(asmSet);

            Set<String> onlyInAsm = new HashSet<>(asmSet);
            onlyInAsm.removeAll(reflectionSet);

            System.out.println("Only Reflection found: " + onlyInReflection);
            System.out.println("Only ASM found: " + onlyInAsm);
        }
    }

    private static Timing measure(Callable<List<String>> scan) throws Exception {
        long firstStart = System.nanoTime();
        scan.call();
        double firstScanMillis = (System.nanoTime() - firstStart) / 1_000_000.0;

        for (int i = 0; i < WARMUP_RUNS; i++) {
            scan.call();
        }

        List<Double> measurements = new ArrayList<>();
        for (int i = 0; i < MEASURED_RUNS; i++) {
            long start = System.nanoTime();
            scan.call();
            measurements.add((System.nanoTime() - start) / 1_000_000.0);
        }

        double average = measurements.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double variance = measurements.stream()
                .mapToDouble(time -> Math.pow(time - average, 2))
                .average()
                .orElse(0);

        return new Timing(firstScanMillis, average, Math.sqrt(variance));
    }

    private static void printRow(String name, Timing timing, int componentCount, String behavior) {
        System.out.printf("| %-14s | %16.3f | %10.3f ± %-9.3f | %14d | %-28s |%n",
                name, timing.firstScanMillis(), timing.averageMillis(),
                timing.standardDeviationMillis(), componentCount, behavior);
    }

    private record Timing(double firstScanMillis, double averageMillis,
                          double standardDeviationMillis) {
    }
}
