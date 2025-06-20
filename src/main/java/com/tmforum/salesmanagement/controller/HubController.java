package com.tmforum.salesmanagement.controller;

import com.tmforum.salesmanagement.model.Hub;
import com.tmforum.salesmanagement.service.HubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/tmf-api/salesManagement/v4")
@CrossOrigin(origins = "*")
public class HubController {

    @Autowired
    private HubService hubService;

    /**
     * POST /hub - Register a new notification hub
     */
    @PostMapping("/hub")
    public ResponseEntity<Hub> registerHub(
            @Valid @RequestBody Hub hub,
            HttpServletRequest request) {

        try {
            // Validate hub data
            if (!hubService.validateHub(hub)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Hub registeredHub = hubService.register(hub);

            // Set location header for the created resource
            String baseUrl = getBaseUrl(request);
            registeredHub.setCallback(hub.getCallback()); // Ensure callback is preserved

            return ResponseEntity.status(HttpStatus.CREATED)
                    .header("Location", baseUrl + "/hub/" + registeredHub.getId())
                    .body(registeredHub);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /hub/{id} - Unregister a notification hub
     */
    @DeleteMapping("/hub/{id}")
    public ResponseEntity<Void> unregisterHub(@PathVariable String id) {
        try {
            boolean deleted = hubService.unregister(id);

            if (deleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /hub - List all registered hubs
     */
    @GetMapping("/hub")
    public ResponseEntity<List<Hub>> listHubs() {
        try {
            List<Hub> hubs = hubService.findAll();
            return ResponseEntity.ok(hubs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /hub/{id} - Get specific hub by ID
     */
    @GetMapping("/hub/{id}")
    public ResponseEntity<Hub> getHub(@PathVariable String id) {
        try {
            Optional<Hub> hub = hubService.findById(id);

            if (hub.isPresent()) {
                return ResponseEntity.ok(hub.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /hub/{id}/test - Test notification to a specific hub
     */
    @PostMapping("/hub/{id}/test")
    public ResponseEntity<Map<String, Object>> testHub(@PathVariable String id) {
        try {
            boolean testResult = hubService.testNotification(id);

            Map<String, Object> response = new HashMap<>();
            response.put("hubId", id);
            response.put("testSent", testResult);
            response.put("timestamp", java.time.LocalDateTime.now());

            if (testResult) {
                response.put("message", "Test notification sent successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Hub not found or test failed");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("hubId", id);
            response.put("testSent", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /hub/stats - Get hub statistics
     */
    @GetMapping("/hub/stats")
    public ResponseEntity<Map<String, Object>> getHubStats() {
        try {
            Map<String, Object> stats = hubService.getHubStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * DELETE /hub - Clear all registered hubs (for testing)
     */
    @DeleteMapping("/hub")
    public ResponseEntity<Map<String, Object>> clearAllHubs() {
        try {
            int previousCount = hubService.getRegisteredHubCount();
            hubService.clearAllHubs();

            Map<String, Object> response = new HashMap<>();
            response.put("message", "All hubs cleared successfully");
            response.put("clearedCount", previousCount);
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /hub/notify - Send custom notification to all hubs (for testing)
     */
    @PostMapping("/hub/notify")
    public ResponseEntity<Map<String, Object>> notifyAllHubs(
            @RequestBody Map<String, Object> notificationData) {

        try {
            String eventType = (String) notificationData.getOrDefault("eventType", "TestNotification");
            Object eventData = notificationData.getOrDefault("eventData", "Test notification data");

            hubService.notifyHubs(eventType, eventData);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Notifications sent to all registered hubs");
            response.put("hubCount", hubService.getRegisteredHubCount());
            response.put("eventType", eventType);
            response.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to send notifications");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET /hub/health - Hub service health check
     */
    @GetMapping("/hub/health")
    public ResponseEntity<Map<String, Object>> hubHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Hub Management Service");
        health.put("registeredHubs", hubService.getRegisteredHubCount());
        health.put("timestamp", java.time.LocalDateTime.now());

        return ResponseEntity.ok(health);
    }

    /**
     * Utility method to construct base URL
     */
    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" +
                request.getServerName() + ":" +
                request.getServerPort() +
                "/tmf-api/salesManagement/v4";
    }
}