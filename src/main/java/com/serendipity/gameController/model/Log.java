package com.serendipity.gameController.model;

import org.aspectj.weaver.ast.Not;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Log {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private LogType type;

    @NotNull
    private Long interactionId;

    @NotNull
    private LocalTime time;

    @ManyToOne
    private Zone zone;

    @NotNull
    private boolean sent;

    public Log() {
    }

    public Log(@NotNull LogType type, @NotNull Long interactionId, @NotNull LocalTime time, @NotNull Zone zone) {
        this.type = type;
        this.interactionId = interactionId;
        this.time = time;
        this.zone = zone;
        this.sent = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogType getType() {
        return type;
    }

    public void setType(LogType type) {
        this.type = type;
    }

    public Long getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(Long interactionId) {
        this.interactionId = interactionId;
    }

    public LocalTime getTime() { return time; }

    public void setTime(LocalTime time) { this.time = time; }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", type=" + type +
                ", interactionId=" + interactionId +
                ", time=" + time +
                ", zone=" + zone +
                ", sent=" + sent +
                '}';
    }

}
