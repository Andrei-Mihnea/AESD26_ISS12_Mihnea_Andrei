package com.example.demo.advanced.scanner.reflection;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectionComponentScanner {
    private static final String COMPONENT_ANNOTATION = "generated.MyComponent";

    public List<String> scan(Path classesDirectory, int classCount) throws Exception{
        List<String> components = new ArrayList<>();

        try (URLClassLoader classLoader = new URLClassLoader(
                new URL[]{classesDirectory.toUri().toURL()},
                getClass().getClassLoader()))
        {
            for(var i = 1; i <= classCount; ++i) {
                String className = "generated.GeneratedClass" + i;

                Class<?> candidate = Class.forName(className, false, classLoader);

                boolean hasComponentAnnotation = Arrays.stream(candidate.getDeclaredAnnotations())
                        .anyMatch(annotation ->
                                annotation.annotationType().getName().equals(COMPONENT_ANNOTATION));

                if (hasComponentAnnotation){
                    components.add(className);
                }
            }
        }

        return components;
    }
}
