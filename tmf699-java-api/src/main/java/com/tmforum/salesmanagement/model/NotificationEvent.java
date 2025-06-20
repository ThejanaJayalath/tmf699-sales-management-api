package com.tmforum.salesmanagement.model;

import java.time.LocalDateTime;

public class NotificationEvent {
    private String eventId;
    private String eventType;
    private LocalDateTime eventTime;
    private EventData event;

    public NotificationEvent() {
        this.eventTime = LocalDateTime.now();
    }

    public NotificationEvent(String eventType, SalesLead salesLead) {
        this.eventType = eventType;
        this.eventTime = LocalDateTime.now();
        this.event = new EventData(salesLead);
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public EventData getEvent() {
        return event;
    }

    public void setEvent(EventData event) {
        this.event = event;
    }

    public static class EventData {
        private SalesLead salesLead;

        public EventData() {}

        public EventData(SalesLead salesLead) {
            this.salesLead = salesLead;
        }

        public SalesLead getSalesLead() {
            return salesLead;
        }

        public void setSalesLead(SalesLead salesLead) {
            this.salesLead = salesLead;
        }
    }
}