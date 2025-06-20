package com.tmforum.salesmanagement.service;

import com.tmforum.salesmanagement.model.SalesLead;
import com.tmforum.salesmanagement.repository.SalesLeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;

@Service
public class SalesLeadService {

    @Autowired
    private SalesLeadRepository salesLeadRepository;

    public List<SalesLead> findAll(Integer offset, Integer limit) {
        try {
            if (offset == null) offset = 0;
            if (limit == null) limit = 50;

            Pageable pageable = PageRequest.of(offset / limit, limit);
            return salesLeadRepository.findAll(pageable).getContent();
        } catch (Exception e) {
            System.err.println("Error in findAll: " + e.getMessage());
            throw e;
        }
    }

    public Optional<SalesLead> findById(String id) {
        try {
            return salesLeadRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error in findById: " + e.getMessage());
            throw e;
        }
    }

    public SalesLead create(SalesLead salesLead) {
        try {
            // Generate String ID first
            String generatedId = generateId();
            salesLead.setId(generatedId);

            // Set default values if not provided
            if (salesLead.getStatus() == null) {
                salesLead.setStatus(SalesLead.Status.LEAD);
            }

            if (salesLead.getPriority() == null) {
                salesLead.setPriority(SalesLead.Priority.MEDIUM);
            }

            if (salesLead.getCreationDate() == null) {
                salesLead.setCreationDate(LocalDateTime.now());
            }

            if (salesLead.getStatusChangeDate() == null) {
                salesLead.setStatusChangeDate(LocalDateTime.now());
            }

            // Set href
            salesLead.setHref("/tmf-api/salesManagement/v4/salesLead/" + generatedId);

            System.out.println("About to save: " + salesLead);

            // Save the entity
            SalesLead saved = salesLeadRepository.save(salesLead);

            System.out.println("Saved successfully: " + saved);

            return saved;
        } catch (Exception e) {
            System.err.println("Error in create: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Optional<SalesLead> update(String id, SalesLead salesLead) {
        try {
            Optional<SalesLead> existing = salesLeadRepository.findById(id);

            if (existing.isPresent()) {
                SalesLead existingSalesLead = existing.get();

                // Update only non-null fields
                if (salesLead.getName() != null) {
                    existingSalesLead.setName(salesLead.getName());
                }

                if (salesLead.getDescription() != null) {
                    existingSalesLead.setDescription(salesLead.getDescription());
                }

                if (salesLead.getPriority() != null) {
                    existingSalesLead.setPriority(salesLead.getPriority());
                }

                if (salesLead.getRating() != null) {
                    existingSalesLead.setRating(salesLead.getRating());
                }

                if (salesLead.getStatus() != null) {
                    // Store old status for comparison
                    SalesLead.Status oldStatus = existingSalesLead.getStatus();
                    SalesLead.Status newStatus = salesLead.getStatus();

                    existingSalesLead.setStatus(newStatus);

                    // Update statusChangeDate when status changes
                    if (!Objects.equals(oldStatus, newStatus)) {
                        existingSalesLead.setStatusChangeDate(LocalDateTime.now());
                    }
                }

                return Optional.of(salesLeadRepository.save(existingSalesLead));
            }

            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error in update: " + e.getMessage());
            throw e;
        }
    }

    public boolean delete(String id) {
        try {
            if (salesLeadRepository.existsById(id)) {
                salesLeadRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error in delete: " + e.getMessage());
            throw e;
        }
    }

    private String generateId() {
        return String.valueOf(System.currentTimeMillis());
    }
}