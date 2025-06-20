package com.tmforum.salesmanagement.service;

import com.tmforum.salesmanagement.model.Hub;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

@Service
public class HubService {

    // In-memory storage for registered hubs (in production, use a database)
    private final Map<String, Hub> registeredHubs = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Register a new hub for notifications
     */
    public Hub register(Hub hub) {
        try {
            // Generate unique ID for the hub
            String hubId = UUID.randomUUID().toString();
            hub.setId(hubId);

            // Validate callback URL
            if (hub.getCallback() == null || hub.getCallback().trim().isEmpty()) {
                throw new IllegalArgumentException("Callback URL is required");
            }

            // Store the hub
            registeredHubs.put(hubId, hub);

            System.out.println("Hub registered successfully: " + hubId + " -> " + hub.getCallback());
            return hub;
        } catch (Exception e) {
            System.err.println("Failed to register hub: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Unregister a hub
     */
    public boolean unregister(String hubId) {
        try {
            Hub removedHub = registeredHubs.remove(hubId);
            if (removedHub != null) {
                System.out.println("Hub unregistered successfully: " + hubId);
                return true;
            } else {
                System.out.println("Hub not found for unregistration: " + hubId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to unregister hub " + hubId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Find hub by ID
     */
    public Optional<Hub> findById(String hubId) {
        return Optional.ofNullable(registeredHubs.get(hubId));
    }

    /**
     * Get all registered hubs
     */
    public List<Hub> findAll() {
        return new ArrayList<>(registeredHubs.values());
    }

    /**
     * Get list of registered hubs (alias for findAll)
     */
    public List<Hub> getRegisteredHubs() {
        return findAll();
    }

    /**
     * Get count of registered hubs
     */
    public int getRegisteredHubCount() {
        return registeredHubs.size();
    }

    /**
     * Notify all registered hubs about an event
     */
    public void notifyHubs(String eventType, Object eventData) {
        if (registeredHubs.isEmpty()) {
            System.out.println("No hubs registered for notification");
            return;
        }

        System.out.println("Notifying " + registeredHubs.size() + " hubs about event: " + eventType);

        // Send notifications asynchronously to all hubs
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Hub hub : registeredHubs.values()) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                notifyHub(hub, eventType, eventData);
            });
            futures.add(future);
        }

        // Wait for all notifications to complete (with timeout)
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .join();
        } catch (Exception e) {
            System.err.println("Some hub notifications may have failed: " + e.getMessage());
        }
    }

    /**
     * Notify a specific hub
     */
    private void notifyHub(Hub hub, String eventType, Object eventData) {
        try {
            // Prepare notification payload
            Map<String, Object> notification = new HashMap<>();
            notification.put("eventType", eventType);
            notification.put("eventTime", LocalDateTime.now().toString());
            notification.put("event", eventData);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("User-Agent", "TMF699-SalesManagement-API/4.0.1");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(notification, headers);

            // Send HTTP POST to the hub callback URL
            try {
                restTemplate.postForEntity(hub.getCallback(), request, String.class);
                System.out.println("Notification sent successfully to hub: " + hub.getId() + " (" + hub.getCallback() + ")");
            } catch (Exception httpError) {
                System.err.println("HTTP notification failed for hub " + hub.getId() +
                        " (" + hub.getCallback() + "): " + httpError.getMessage());

                // In production, you might want to:
                // 1. Retry with exponential backoff
                // 2. Move to dead letter queue
                // 3. Unregister hub after multiple failures
            }

        } catch (Exception e) {
            System.err.println("Failed to notify hub " + hub.getId() + ": " + e.getMessage());
        }
    }

    /**
     * Notify specific hub by ID
     */
    public boolean notifyHubById(String hubId, String eventType, Object eventData) {
        Hub hub = registeredHubs.get(hubId);
        if (hub != null) {
            notifyHub(hub, eventType, eventData);
            return true;
        } else {
            System.err.println("Hub not found for notification: " + hubId);
            return false;
        }
    }

    /**
     * Test notification to a specific hub
     */
    public boolean testNotification(String hubId) {
        Hub hub = registeredHubs.get(hubId);
        if (hub != null) {
            Map<String, Object> testData = new HashMap<>();
            testData.put("message", "Test notification from TMF699 Sales Management API");
            testData.put("timestamp", LocalDateTime.now().toString());

            notifyHub(hub, "TestEvent", testData);
            return true;
        }
        return false;
    }

    /**
     * Clear all registered hubs
     */
    public void clearAllHubs() {
        int count = registeredHubs.size();
        registeredHubs.clear();
        System.out.println("Cleared " + count + " registered hubs");
    }

    /**
     * Get hub statistics
     */
    public Map<String, Object> getHubStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_hubs", registeredHubs.size());
        stats.put("timestamp", LocalDateTime.now().toString());

        // Group by callback domain for statistics
        Map<String, Integer> domainCount = new HashMap<>();
        for (Hub hub : registeredHubs.values()) {
            try {
                String domain = new java.net.URL(hub.getCallback()).getHost();
                domainCount.put(domain, domainCount.getOrDefault(domain, 0) + 1);
            } catch (Exception e) {
                domainCount.put("invalid_url", domainCount.getOrDefault("invalid_url", 0) + 1);
            }
        }
        stats.put("domains", domainCount);

        return stats;
    }

    /**
     * Validate hub registration data
     */
    public boolean validateHub(Hub hub) {
        if (hub.getCallback() == null || hub.getCallback().trim().isEmpty()) {
            return false;
        }

        try {
            new java.net.URL(hub.getCallback()); // Validate URL format
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clean up inactive hubs (placeholder for future implementation)
     */
    public void cleanupInactiveHubs() {
        // In production, you would:
        // 1. Track last successful notification time
        // 2. Remove hubs that haven't responded in X days
        // 3. Send health check pings periodically
        System.out.println("Hub cleanup placeholder - implement health checking in production");
    }
}