package com.tmforum.salesmanagement.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RootController {

    /**
     * Root endpoint - redirects to API info
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to TMF699 Sales Management API");
        response.put("version", "4.0.1");
        response.put("specification", "TMF699 Sales Management API REST Specification");
        response.put("apiBase", "/tmf-api/salesManagement/v4");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("API Info", "/tmf-api/salesManagement/v4");
        endpoints.put("Health Check", "/tmf-api/salesManagement/v4/health");
        endpoints.put("Sales Leads", "/tmf-api/salesManagement/v4/salesLead");
        endpoints.put("Hub Management", "/tmf-api/salesManagement/v4/hub");

        response.put("availableEndpoints", endpoints);

        Map<String, String> examples = new HashMap<>();
        examples.put("Get all leads", "GET /tmf-api/salesManagement/v4/salesLead");
        examples.put("Create lead", "POST /tmf-api/salesManagement/v4/salesLead");
        examples.put("Get lead by ID", "GET /tmf-api/salesManagement/v4/salesLead/{id}");
        examples.put("Update lead", "PATCH /tmf-api/salesManagement/v4/salesLead/{id}");
        examples.put("Delete lead", "DELETE /tmf-api/salesManagement/v4/salesLead/{id}");

        response.put("examples", examples);

        return ResponseEntity.ok(response);
    }

    /**
     * Simple health check at root level
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> simpleHealth() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("message", "TMF699 Sales Management API is running");
        health.put("detailedHealth", "/tmf-api/salesManagement/v4/health");
        return ResponseEntity.ok(health);
    }

    /**
     * API documentation endpoint
     */
    @GetMapping("/api-docs")
    public ResponseEntity<Map<String, Object>> apiDocs() {
        Map<String, Object> docs = new HashMap<>();
        docs.put("title", "TMF699 Sales Management API");
        docs.put("version", "4.0.1");
        docs.put("description", "TM Forum Sales Management API for managing sales leads, opportunities, and notifications");

        Map<String, Object> resources = new HashMap<>();

        // Sales Lead Resource
        Map<String, Object> salesLead = new HashMap<>();
        salesLead.put("description", "Manage sales leads");
        salesLead.put("basePath", "/tmf-api/salesManagement/v4/salesLead");
        salesLead.put("operations", Map.of(
                "GET", "List sales leads with optional filtering",
                "POST", "Create a new sales lead",
                "GET /{id}", "Retrieve specific sales lead",
                "PATCH /{id}", "Partially update sales lead",
                "DELETE /{id}", "Delete sales lead"
        ));
        resources.put("salesLead", salesLead);

        // Hub Resource
        Map<String, Object> hub = new HashMap<>();
        hub.put("description", "Manage notification hubs");
        hub.put("basePath", "/tmf-api/salesManagement/v4/hub");
        hub.put("operations", Map.of(
                "GET", "List registered hubs",
                "POST", "Register new hub for notifications",
                "DELETE /{id}", "Unregister hub"
        ));
        resources.put("hub", hub);

        docs.put("resources", resources);

        // Sample requests
        Map<String, Object> samples = new HashMap<>();
        samples.put("createSalesLead", Map.of(
                "method", "POST",
                "url", "/tmf-api/salesManagement/v4/salesLead",
                "body", Map.of(
                        "name", "Test Sales Lead",
                        "description", "Sample sales lead for testing",
                        "priority", "medium",
                        "rating", "hot"
                )
        ));

        samples.put("registerHub", Map.of(
                "method", "POST",
                "url", "/tmf-api/salesManagement/v4/hub",
                "body", Map.of(
                        "callback", "http://your-server.com/notifications"
                )
        ));

        docs.put("examples", samples);

        return ResponseEntity.ok(docs);
    }

    /**
     * Status endpoint
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "TMF699 Sales Management API");
        status.put("status", "RUNNING");
        status.put("timestamp", java.time.LocalDateTime.now());
        status.put("version", "4.0.1");
        status.put("port", "8081");

        return ResponseEntity.ok(status);
    }
}