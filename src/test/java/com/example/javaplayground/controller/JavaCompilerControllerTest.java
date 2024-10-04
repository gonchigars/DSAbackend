package com.example.javaplayground.controller;

import com.example.javaplayground.service.JavaCompilerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JavaCompilerController.class)
public class JavaCompilerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JavaCompilerService javaCompilerService;

    @Test
    public void testCompileAndRun() throws Exception {
        String testCode = "public class Test { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }";
        String expectedOutput = "Hello, World!";

        when(javaCompilerService.compileAndRun(anyString())).thenReturn(expectedOutput);

        mockMvc.perform(post("/compile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCode))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedOutput));
    }
}