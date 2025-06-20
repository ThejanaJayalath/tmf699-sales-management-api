package com.tmforum.salesmanagement.service;

import com.tmforum.salesmanagement.model.SalesLead;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final List<Map<String, String>> subscriptions = new CopyOnWriteArrayList<>();

    // Subscribe to notifications (using simple Map)
    public Map<String, String> subscribe(String callback, String query) {
        Map<String, String> subscription = new HashMap<>();
        subscription.put("id", generateId());
        subscription.put("callback", callback);
        subscription.put("query", query != null ? query : "");
        subscription.put("createdDate", LocalDateTime.now().toString());

        subscriptions.add(subscription);
        return subscription;
    }

    // Unsubscribe from notifications
    public boolean unsubscribe(String id) {
        return subscriptions.removeIf(sub -> id.equals(sub.get("id")));
    }

    // Get all subscriptions
    public List<Map<String, String>> getSubscriptions() {
        return subscriptions;
    }

    // Send notification when sales lead is created
    public void notifySalesLeadCreated(SalesLead salesLead) {
        sendNotification("SalesLeadCreateEvent", salesLead, "created");
    }

    // Send notification when sales lead is updated
    public void notifySalesLeadUpdated(SalesLead salesLead) {
        sendNotification("SalesLeadAttributeValueChangeEvent", salesLead, "updated");
    }

    // Send notification when sales lead is deleted
    public void notifySalesLeadDeleted(String salesLeadId) {
        SalesLead placeholder = new SalesLead();
        placeholder.setId(salesLeadId);
        sendNotification("SalesLeadDeleteEvent", placeholder, "deleted");
    }

    // Send notification when sales lead status changes
    public void notifySalesLeadStatusChanged(SalesLead salesLead, SalesLead.Status oldStatus, SalesLead.Status newStatus) {
        Map<String, Object> eventData = createEventData("SalesLeadStateChangeEvent", salesLead, "statusChanged");

        // Add status change specific data
        eventData.put("fieldPath", "status");
        eventData.put("oldValue", oldStatus != null ? oldStatus.name() : null);
        eventData.put("newValue", newStatus != null ? newStatus.name() : null);

        sendNotificationToSubscribers(eventData);
    }

    // Generic method to send notifications
    private void sendNotification(String eventType, SalesLead salesLead, String action) {
        Map<String, Object> eventData = createEventData(eventType, salesLead, action);
        sendNotificationToSubscribers(eventData);
    }

    // Create event data structure
    private Map<String, Object> createEventData(String eventType, SalesLead salesLead, String action) {
        Map<String, Object> eventData = new HashMap<>();

        // Event metadata
        eventData.put("eventId", generateEventId());
        eventData.put("eventTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        eventData.put("eventType", eventType);
        eventData.put("correlationId", generateCorrelationId());
        eventData.put("domain", "sales");
        eventData.put("title", "Sales Lead " + action);
        eventData.put("description", "Sales lead has been " + action);
        eventData.put("priority", "Normal");
        eventData.put("timeOcurred", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // Event source
        Map<String, Object> source = new HashMap<>();
        source.put("href", salesLead.getHref());
        source.put("id", salesLead.getId());
        source.put("name", salesLead.getName());
        eventData.put("source", source);

        // Event subject (the sales lead data)
        Map<String, Object> subject = new HashMap<>();
        subject.put("id", salesLead.getId());
        subject.put("href", salesLead.getHref());
        subject.put("name", salesLead.getName());
        subject.put("description", salesLead.getDescription());
        subject.put("status", salesLead.getStatus() != null ? salesLead.getStatus().name() : null);
        subject.put("priority", salesLead.getPriority() != null ? salesLead.getPriority().name() : null);
        subject.put("rating", salesLead.getRating());
        subject.put("creationDate", salesLead.getCreationDate());
        subject.put("@type", "SalesLead");
        eventData.put("event", subject);

        return eventData;
    }

    // Send notifications to all subscribers
    private void sendNotificationToSubscribers(Map<String, Object> eventData) {
        for (Map<String, String> subscription : subscriptions) {
            try {
                String callback = subscription.get("callback");
                sendNotificationToCallback(callback, eventData);
            } catch (Exception e) {
                System.err.println("Failed to send notification to: " + subscription.get("callback"));
                System.err.println("Error: " + e.getMessage());
                // Continue with other subscriptions even if one fails
            }
        }
    }

    // Send HTTP POST notification to callback URL
    private void sendNotificationToCallback(String callbackUrl, Map<String, Object> eventData) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(eventData, headers);

            restTemplate.postForEntity(callbackUrl, request, String.class);
            System.out.println("Notification sent successfully to: " + callbackUrl);

        } catch (Exception e) {
            System.err.println("Failed to send notification to: " + callbackUrl);
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    // Utility methods for generating IDs
    private String generateId() {
        return "sub_" + System.currentTimeMillis();
    }

    private String generateEventId() {
        return "evt_" + System.currentTimeMillis();
    }

    private String generateCorrelationId() {
        return "corr_" + System.currentTimeMillis();
    }

    // Check if there are any subscribers
    public boolean hasSubscribers() {
        return !subscriptions.isEmpty();
    }

    // Get subscription by ID
    public Map<String, String> getSubscription(String id) {
        return subscriptions.stream()
                .filter(sub -> id.equals(sub.get("id")))
                .findFirst()
                .orElse(null);
    }

    // Clear all subscriptions (useful for testing)
    public void clearSubscriptions() {
        subscriptions.clear();
    }
}