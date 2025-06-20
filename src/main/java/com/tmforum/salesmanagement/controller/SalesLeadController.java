package com.tmforum.salesmanagement.controller;

import com.tmforum.salesmanagement.model.SalesLead;
import com.tmforum.salesmanagement.service.SalesLeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tmf-api/salesManagement/v4")
@Validated
@CrossOrigin(origins = "*")
public class SalesLeadController {

    @Autowired
    private SalesLeadService salesLeadService;

    // GET all sales leads
    @GetMapping("/salesLead")
    public ResponseEntity<?> getAllSalesLeads(
            @RequestParam(required = false) String fields,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "50") Integer limit) {

        try {
            System.out.println("GET /salesLead - Getting all sales leads");
            List<SalesLead> salesLeads = salesLeadService.findAll(offset, limit);
            System.out.println("Found " + salesLeads.size() + " sales leads");
            return ResponseEntity.ok(salesLeads);
        } catch (Exception e) {
            System.err.println("Error getting sales leads: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // GET specific sales lead by ID
    @GetMapping("/salesLead/{id}")
    public ResponseEntity<?> getSalesLead(@PathVariable String id) {
        try {
            System.out.println("GET /salesLead/" + id);
            Optional<SalesLead> salesLead = salesLeadService.findById(id);

            if (salesLead.isPresent()) {
                return ResponseEntity.ok(salesLead.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Sales lead not found with id: " + id + "\"}");
            }
        } catch (Exception e) {
            System.err.println("Error getting sales lead: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // CREATE new sales lead
    @PostMapping("/salesLead")
    public ResponseEntity<?> createSalesLead(@Valid @RequestBody SalesLead salesLead) {
        try {
            System.out.println("POST /salesLead - Creating sales lead");
            System.out.println("Received data: " + salesLead);

            // Validate required fields manually if needed
            if (salesLead.getName() == null || salesLead.getName().trim().isEmpty()) {
                System.err.println("Name validation failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Name is required\"}");
            }

            SalesLead created = salesLeadService.create(salesLead);
            System.out.println("Created sales lead successfully: " + created);

            // Verify all required fields are present
            if (created.getId() == null) {
                System.err.println("ERROR: Created sales lead has null ID");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\"Failed to generate ID\"}");
            }

            if (created.getName() == null) {
                System.err.println("ERROR: Created sales lead has null name");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\":\"Name was not saved properly\"}");
            }

            // Return 201 CREATED for POST requests (CTK requirement)
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            System.err.println("Error creating sales lead: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"Internal server error: " + e.getMessage() + "\"}");
        }
    }

    // UPDATE sales lead
    @PatchMapping("/salesLead/{id}")
    public ResponseEntity<?> updateSalesLead(@PathVariable String id, @RequestBody SalesLead salesLead) {
        try {
            System.out.println("PATCH /salesLead/" + id);
            Optional<SalesLead> updated = salesLeadService.update(id, salesLead);

            if (updated.isPresent()) {
                return ResponseEntity.ok(updated.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Sales lead not found with id: " + id + "\"}");
            }
        } catch (Exception e) {
            System.err.println("Error updating sales lead: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // DELETE sales lead
    @DeleteMapping("/salesLead/{id}")
    public ResponseEntity<?> deleteSalesLead(@PathVariable String id) {
        try {
            System.out.println("DELETE /salesLead/" + id);
            boolean deleted = salesLeadService.delete(id);

            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"error\":\"Sales lead not found with id: " + id + "\"}");
            }
        } catch (Exception e) {
            System.err.println("Error deleting sales lead: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}