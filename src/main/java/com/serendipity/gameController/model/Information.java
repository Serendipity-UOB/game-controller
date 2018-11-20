package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Information {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private Player owner;

    @OneToOne
    private Player contact;

    private int interactions;

    public Information() {
    }

    public Information(Player owner, Player contact, int interactions) {
        this.owner = owner;
        this.contact = contact;
        this.interactions = interactions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public Player getContact() {
        return contact;
    }

    public void setContact(Player contact) {
        this.contact = contact;
    }

    public int getInteractions() {
        return interactions;
    }

    public void setInteractions(int interactions) {
        this.interactions = interactions;
    }
}
