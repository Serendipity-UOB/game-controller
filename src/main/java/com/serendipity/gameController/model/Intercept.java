package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Intercept {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Player intercepter;

    @ManyToOne
    private Exchange exchange;

    private boolean expired;

    public Intercept() {
    }

    public Intercept(Player intercepter, Exchange exchange) {
        this.intercepter = intercepter;
        this.exchange = exchange;
        this.expired = false;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Player getIntercepter() { return intercepter; }

    public void setIntercepter(Player intercepter) { this.intercepter = intercepter; }

    public Exchange getExchange() { return exchange; }

    public void setExchange(Exchange exchange) { this.exchange = exchange; }

    public boolean isExpired() { return expired; }

    public void setExpired(boolean expired) { this.expired = expired; }

    @Override
    public String toString() {
        return "Intercept{" +
                "id=" + id +
                ", intercepter=" + intercepter +
                ", exchange=" + exchange +
                ", expired=" + expired +
                '}';
    }
}
