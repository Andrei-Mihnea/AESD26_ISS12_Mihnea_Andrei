package com.example.demo.advanced.scanner;

import com.example.demo.advanced.scanner.asm.AsmComponentScanner;

import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

public class MissingDependencyDemo {
    public static void main(String[] args) throws Exception {
        Path projectDirectory = Path.of("component-scan-experiment");
        Path classesDirectory = projectDirectory.resolve("classes");
        Path sourceDirectory = projectDirectory.resolve("missing-dependency/sources");
        Path compileDirectory = projectDirectory.resolve("missing-dependency/compiled");
        Path runtimeDirectory = projectDirectory.resolve("missing-dependency/runtime-classes");
        Path packageDirectory = sourceDirectory.resolve("missingdep");

        Files.createDirectories(packageDirectory);
        Files.createDirectories(compileDirectory);
        Files.createDirectories(runtimeDirectory.resolve("missingdep"));

        Path dependencySource = packageDirectory.resolve("MissingDependencyBase.java");
        Path componentSource = packageDirectory.resolve("MissingDependencyComponent.java");
        Files.writeString(dependencySource, """
                package missingdep;
                public class MissingDependencyBase { }
                """);
        Files.writeString(componentSource, """
                package missingdep;
                import generated.MyComponent;
                @MyComponent
                public class MissingDependencyComponent extends MissingDependencyBase { }
                """);

        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Run this with a JDK, not a JRE");
        }

        String classPath = System.getProperty("java.class.path")
                + System.getProperty("path.separator") + classesDirectory.toAbsolutePath();
        int compileResult = compiler.run(null, System.out, System.err,
                "-classpath", classPath,
                "-d", compileDirectory.toString(),
                dependencySource.toString(), componentSource.toString());
        if (compileResult != 0) {
            throw new IllegalStateException("Could not compile missing-dependency example");
        }

        Path componentClass = compileDirectory.resolve("missingdep/MissingDependencyComponent.class");
        Path runtimeComponent = runtimeDirectory.resolve("missingdep/MissingDependencyComponent.class");
        Files.copy(componentClass, runtimeComponent, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.deleteIfExists(runtimeDirectory.resolve("missingdep/MissingDependencyBase.class"));

        System.out.println("Reflection:");
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{
                runtimeDirectory.toUri().toURL(), classesDirectory.toUri().toURL()
        }, getClassLoader())) {
            Class.forName("missingdep.MissingDependencyComponent", false, classLoader);
            System.out.println("  Unexpectedly loaded the class");
        } catch (NoClassDefFoundError error) {
            System.out.println("  Failed to load candidate because its base class is unavailable: "
                    + error.getMessage());
        }

        System.out.println("ASM:");
        var components = new AsmComponentScanner().scan(runtimeDirectory);
        System.out.println("  Found annotated class without loading it: " + components);
    }

    private static ClassLoader getClassLoader() {
        return MissingDependencyDemo.class.getClassLoader();
    }
}
