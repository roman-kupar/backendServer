package org.example.medaibackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ChatbotController {

    @Autowired
    private StatusWebSocketHandler statusWebSocketHandler;

    @PostMapping("/ask")
    public ResponseEntity<String> handleQuestion(@RequestBody QuestionDTO question)
    {
        try {
            statusWebSocketHandler.broadcastStatus("Processing your question...");
        } catch (Exception e) {
            return null;
        }

        String answer = ServiceML.getAnswerFromText(question.getText());

        try {
            statusWebSocketHandler.broadcastStatus("Answer ready!");
        } catch (Exception e) {
            return null;
        }

        return ResponseEntity.ok(answer);
    }

    @PostMapping("/upload-pdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
                return ResponseEntity.badRequest().body("Only PDF files are allowed.");
            }

            Path uploadDir = Paths.get("src/main/resources/uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String uniqueFilename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            System.out.println(uniqueFilename + "generated");

            Path filePath = uploadDir.resolve(uniqueFilename);
            file.transferTo(filePath);

            String answer = ServiceML.getAnswerFromPdf(uniqueFilename);

            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("File upload failed.");
        }
    }
}
