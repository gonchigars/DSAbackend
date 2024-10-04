package com.example.javaplayground.service;

import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class JavaCompilerService {

    public String compileAndRun(String sourceCode) throws Exception {
        // Compile the code
        Class<?> compiledClass = compileCode(sourceCode);

        // Capture the output
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);

        try {
            // Find and invoke the main method
            Method mainMethod = compiledClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[0]);
        } finally {
            System.out.flush();
            System.setOut(old);
        }

        return baos.toString();
    }

    private Class<?> compileCode(String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        JavaFileObject file = new JavaSourceFromString("DynamicClass", sourceCode);
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        if (!success) {
            StringBuilder sb = new StringBuilder();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                sb.append(diagnostic.toString()).append("\n");
            }
            throw new Exception("Compilation failed:\n" + sb.toString());
        }

        // Load and return the compiled class
        MemoryClassLoader classLoader = new MemoryClassLoader(fileManager.getClassBytes());
        return classLoader.loadClass("DynamicClass");
    }

    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    static class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, ByteArrayOutputStream> classBytes = new HashMap<>();

        MemoryJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            if (kind == JavaFileObject.Kind.CLASS) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                classBytes.put(className, outputStream);
                return new SimpleJavaFileObject(URI.create(className), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        return outputStream;
                    }
                };
            }
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }

        Map<String, byte[]> getClassBytes() {
            Map<String, byte[]> result = new HashMap<>();
            for (Map.Entry<String, ByteArrayOutputStream> entry : classBytes.entrySet()) {
                result.put(entry.getKey(), entry.getValue().toByteArray());
            }
            return result;
        }
    }

    static class MemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> classBytes;

        MemoryClassLoader(Map<String, byte[]> classBytes) {
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classBytes.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}