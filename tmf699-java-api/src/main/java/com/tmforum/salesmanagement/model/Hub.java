package com.tmforum.salesmanagement.model;

import javax.persistence.*;

@Entity
@Table(name = "hub")
public class Hub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String callback;
    private String query;

    public Hub() {}

    public Hub(String callback) {
        this.callback = callback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}