package com.dinesh.ai_job_platform.service;

import com.dinesh.ai_job_platform.exception.FileParsingException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResumeParsingServiceTest {

    private final ResumeParsingService resumeParsingService = new ResumeParsingService();

    @Test
    void shouldThrowWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "resume.txt", "text/plain", new byte[0]);

        FileParsingException exception = assertThrows(FileParsingException.class,
                () -> resumeParsingService.extractText(emptyFile));

        assertEquals("Uploaded file is empty", exception.getMessage());
    }

    @Test
    void shouldExtractTextFromPlainTextResume() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.txt",
                "text/plain",
                "Java Spring Boot\nPostgreSQL".getBytes()
        );

        String text = resumeParsingService.extractText(file);

        assertEquals("Java Spring Boot\nPostgreSQL", text);
    }
}
