package com.tmforum.salesmanagement.repository;

import com.tmforum.salesmanagement.model.SalesLead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesLeadRepository extends JpaRepository<SalesLead, String> {

    // Find by status
    List<SalesLead> findByStatus(SalesLead.Status status);

    // Find by priority
    List<SalesLead> findByPriority(SalesLead.Priority priority);

    // Find by rating
    List<SalesLead> findByRating(String rating);

    // Find by name containing (case-insensitive)
    List<SalesLead> findByNameContainingIgnoreCase(String name);

    // Custom query to find by multiple criteria
    @Query("SELECT s FROM SalesLead s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:priority IS NULL OR s.priority = :priority) AND " +
            "(:rating IS NULL OR s.rating = :rating)")
    Page<SalesLead> findByCriteria(SalesLead.Status status,
                                   SalesLead.Priority priority,
                                   String rating,
                                   Pageable pageable);
}