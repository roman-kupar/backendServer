package org.example.medaibackend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;

@Service
public class ServiceML {
    // Injecting the WebSocketHandler to broadcast status updates
    public static StatusWebSocketHandler statusWebSocketHandler;

    @Autowired
    public ServiceML(StatusWebSocketHandler statusWebSocketHandler) {
        ServiceML.statusWebSocketHandler = statusWebSocketHandler;
    }

    public static String getAnswerFromText(String prompt) {
        try {
            // Broadcast "Processing" status to the frontend via WebSocket
            // statusWebSocketHandler.broadcastStatus("Processing your question...");

            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "med.ai/pythonProject/main.py",
                    prompt
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            boolean captureAnswer = false;
            boolean isAnswering = false;
            String line;

            // Read the Python process output
            while ((line = reader.readLine()) != null) {
                // Ignore HTTP Request lines
                if (line.contains("HTTP Request")) {
                    continue;
                }

                // Broadcast key statuses
                if (line.trim().equals("===ANSWER===")) {
                    statusWebSocketHandler.broadcastStatus("Answer is being processed...");
                    captureAnswer = true;
                    isAnswering = true;
                    continue; // Skip this line, start capturing from the next one
                } else if (!isAnswering) {
                    // Only remove the first 15 characters for WebSocket broadcasting
                    if (line.length() > 15) {
                        String broadcastLine = line.substring(15).trim(); // Remove the first 15 characters
                        // Broadcast the line with the first 15 characters removed
                        statusWebSocketHandler.broadcastStatus(broadcastLine);
                    } else {
                        // Broadcast the line without modification if it's shorter than 15 characters
                        statusWebSocketHandler.broadcastStatus(line);
                    }
                }

                // Append the line to output (don't modify the original line)
                if (captureAnswer) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Python script finished with exit code: " + exitCode);

            // Once processing is complete, notify that the answer is ready
            statusWebSocketHandler.broadcastStatus("Answer ready!");

            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            // statusWebSocketHandler.broadcastStatus("Failed to process your question.");
            return "Failed to process your question.";
        }
    }



    public static String getAnswerFromPdf(String fileName)
    {
        return "some answer from pdf";
    }
}
