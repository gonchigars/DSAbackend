package com.example.javaplayground.controller;

import com.example.javaplayground.service.JavaCompilerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JavaCompilerController {

    private final JavaCompilerService javaCompilerService;

    @Autowired
    public JavaCompilerController(JavaCompilerService javaCompilerService) {
        this.javaCompilerService = javaCompilerService;
    }

    @PostMapping("/compile")
    public ResponseEntity<String> compileAndRun(@RequestBody String code) {
        try {
            String result = javaCompilerService.compileAndRun(code);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Compilation or execution error: " + e.getMessage());
        }
    }
}