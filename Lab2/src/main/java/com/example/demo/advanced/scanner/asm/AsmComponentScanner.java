package com.example.demo.advanced.scanner.asm;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AsmComponentScanner {
    public List<String> scan(Path classesDirectory) throws IOException {
        List<String> components = new ArrayList<>();

        try (var files = Files.walk(classesDirectory)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".class")).toList()) {
                String[] className = new String[1];
                boolean[] hasComponentAnnotation = new boolean[1];

                ClassReader reader = new ClassReader(Files.readAllBytes(file));

                reader.accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public void visit(
                            int version,
                            int access,
                            String name,
                            String signature,
                            String superName,
                            String[] interfaces) {
                        className[0] = name.replace('/', '.');
                    }

                    @Override
                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                        if (descriptor.equals("Lgenerated/MyComponent;")) {
                            hasComponentAnnotation[0] = true;
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

                if (hasComponentAnnotation[0]) {
                    components.add(className[0]);
                }
            }
        }

        return components;
    }
}