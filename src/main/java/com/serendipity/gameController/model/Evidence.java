package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Evidence {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @ManyToOne
    private Exchange exchange;

    @NotNull
    @ManyToOne
    private Player player;

    @NotNull
    private int amount;

    public Evidence(@NotNull Exchange exchange, @NotNull Player player, @NotNull int amount) {
        this.exchange = exchange;
        this.player = player;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
