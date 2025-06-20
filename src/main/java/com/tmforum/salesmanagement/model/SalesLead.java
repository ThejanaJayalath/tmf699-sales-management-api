package com.tmforum.salesmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales_lead")
public class SalesLead {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "href")
    private String href;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Column(name = "rating")
    private String rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @Column(name = "status_change_date")
    private LocalDateTime statusChangeDate;

    // Constructors
    public SalesLead() {
        this.creationDate = LocalDateTime.now();
        this.statusChangeDate = LocalDateTime.now();
        this.status = Status.LEAD;
        this.priority = Priority.MEDIUM;
    }

    public SalesLead(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Enums
    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum Status {
        LEAD, PROSPECT, QUALIFIED, CONVERTED, LOST
    }

    // TMF Metadata (computed properties, not stored in DB)
    @JsonProperty("@type")
    public String getType() {
        return "SalesLead";
    }

    @JsonProperty("@baseType")
    public String getBaseType() {
        return "Entity";
    }

    @JsonProperty("@schemaLocation")
    public String getSchemaLocation() {
        return "https://schemas.tmforum.org/salesManagement/v4/schema/json/salesLead.schema.json";
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        // Auto-generate href when id is set
        if (id != null) {
            this.href = "/tmf-api/salesManagement/v4/salesLead/" + id;
        }
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        Status oldStatus = this.status;
        this.status = status;
        // Update statusChangeDate when status changes
        if (oldStatus != status && status != null) {
            this.statusChangeDate = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getStatusChangeDate() {
        return statusChangeDate;
    }

    public void setStatusChangeDate(LocalDateTime statusChangeDate) {
        this.statusChangeDate = statusChangeDate;
    }

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (statusChangeDate == null) {
            statusChangeDate = LocalDateTime.now();
        }
        if (status == null) {
            status = Status.LEAD;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }

    @Override
    public String toString() {
        return "SalesLead{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", rating='" + rating + '\'' +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", statusChangeDate=" + statusChangeDate +
                '}';
    }
}