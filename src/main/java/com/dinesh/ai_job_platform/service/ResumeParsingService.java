package com.dinesh.ai_job_platform.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

@Service
public class ResumeParsingService {

    public String extractText(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty");
        }

        try (InputStream inputStream = file.getInputStream()) {

            AutoDetectParser parser = new AutoDetectParser();

            // -1 removes character limit
            ContentHandler handler = new BodyContentHandler(-1);

            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(inputStream, handler, metadata, context);

            String text = handler.toString();

            if (text == null || text.trim().isEmpty()) {
                throw new RuntimeException("Unable to extract text from resume");
            }

            return text.trim();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse resume file", e);
        }
    }
}