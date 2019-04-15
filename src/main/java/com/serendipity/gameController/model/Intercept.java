package com.serendipity.gameController.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
public class Intercept {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Player intercepter;

    @ManyToOne
    private Player target;

    @ManyToOne
    private Exchange exchange;

    private LocalTime startTime;

    private boolean expired;

    public Intercept() {
    }

    public Intercept(Player intercepter, Player target, LocalTime startTime) {
        this.intercepter = intercepter;
        this.target = target;
        this.exchange = null;
        this.startTime = startTime;
        this.expired = false;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Player getIntercepter() { return intercepter; }

    public void setIntercepter(Player intercepter) { this.intercepter = intercepter; }

    public Player getTarget() { return target; }

    public void setTarget(Player target) { this.target = target; }

    public Exchange getExchange() { return exchange; }

    public void setExchange(Exchange exchange) { this.exchange = exchange; }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public boolean isExpired() { return expired; }

    public void setExpired(boolean expired) { this.expired = expired; }

    @Override
    public String toString() {
        return "Intercept{" +
                "id=" + id +
                ", intercepter=" + intercepter +
                ", target=" + target +
                ", exchange=" + exchange +
                ", startTime=" + startTime +
                ", expired=" + expired +
                '}';
    }
}
