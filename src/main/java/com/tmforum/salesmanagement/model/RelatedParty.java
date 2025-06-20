package com.tmforum.salesmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

@Entity
@Table(name = "related_party")
public class RelatedParty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String href;
    private String name;
    private String role;

    @JsonProperty("@referredType")
    private String referredType;

    public RelatedParty() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getReferredType() {
        return referredType;
    }

    public void setReferredType(String referredType) {
        this.referredType = referredType;
    }
}