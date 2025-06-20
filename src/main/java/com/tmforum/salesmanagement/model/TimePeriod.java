package com.tmforum.salesmanagement.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Embeddable
public class TimePeriod {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public TimePeriod() {}

    public TimePeriod(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}