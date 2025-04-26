package org.example.medaibackend;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Path;
import java.util.Map;

public class ServiceML {
    public static String getAnswerFromText(String input) {
        // Call Python model server
        return "some answer from text";
    }

    public static String getAnswerFromPdf(String fileName)
    {
        return "some answer from pdf";
    }
}
