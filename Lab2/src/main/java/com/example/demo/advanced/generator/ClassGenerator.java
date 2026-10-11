package com.example.demo.advanced.generator;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClassGenerator {
    public static void main(String[] args) throws Exception {
        int count = Integer.parseInt(args[0]);

        if (count < 500 || count > 2000) {
            throw new IllegalArgumentException("Choose a number from 500 to 2000");
        }

        Path projectRoot = findProjectRoot();
        Path sourceDir = projectRoot.resolve("component-scan-experiment/sources/generated");
        Path annotationFile = sourceDir.resolve("MyComponent.java");
        Path outputDir = projectRoot.resolve("component-scan-experiment/classes");

        Files.createDirectories(sourceDir);
        Files.createDirectories(outputDir);

        String[] compilerArgs = new String[count + 3];
        compilerArgs[0] = "-d";
        compilerArgs[1] = outputDir.toString();
        compilerArgs[2] = annotationFile.toString();

        for (int i = 1; i <= count; i++) {
            String className = "GeneratedClass" + i;
            Path sourceFile = sourceDir.resolve(className + ".java");

            String annotation = (i % 5 == 0) ? "@MyComponent\n" : "";
            String staticInitializer = i <= 3
                    ? "    static { System.out.println(\"Initialized " + className + "\"); }\n"
                    : "";

            Files.writeString(sourceFile, """
            package generated;

            %spublic class %s {
            %s    public int value() {
                    return %d;
                }
            }
            """.formatted(annotation, className, staticInitializer, i));

            compilerArgs[i + 2] = sourceFile.toString();

        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Run this with a JDK, not a JRE");
        }

        int result = compiler.run(
                null, System.out, System.err, compilerArgs);

        if (result != 0) {
            throw new IllegalStateException("Compilation failed");
        }

        System.out.println("Generated and compiled " + count + " classes.");
    }

    private static Path findProjectRoot() {
        Path current = Path.of("").toAbsolutePath().normalize();

        while (current != null) {
            if (Files.isRegularFile(current.resolve("pom.xml"))
                    && Files.isDirectory(current.resolve("src/main/java"))) {
                return current;
            }
            current = current.getParent();
        }

        throw new IllegalStateException("Could not find the Lab2 project root");
    }
}
