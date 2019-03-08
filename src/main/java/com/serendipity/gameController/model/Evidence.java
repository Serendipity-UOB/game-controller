package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Evidence {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Exchange exchange;

    @ManyToOne
    private Player playerAbout;

    @ManyToOne
    private Player author;

    private int amount;

    public Evidence() {
    }

    public Evidence(Exchange exchange, Player playerAbout, Player author, int amount) {
        this.exchange = exchange;
        this.playerAbout = playerAbout;
        this.author = author;
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

    public Player getPlayerAbout() {
        return playerAbout;
    }

    public void setPlayerAbout(Player playerAbout) {
        this.playerAbout = playerAbout;
    }

    public Player getAuthor() {
        return author;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
