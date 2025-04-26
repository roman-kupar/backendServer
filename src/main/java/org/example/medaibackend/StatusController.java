package org.example.medaibackend;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

    private final StatusSocketHandler socketHandler;

    public StatusController(StatusSocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @PostMapping("/status")
    public ResponseEntity<String> receiveStatus(@RequestBody String status) {
        socketHandler.broadcastStatus(status);
        return ResponseEntity.ok("Status broadcasted");
    }
}
