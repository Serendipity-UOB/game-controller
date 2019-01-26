package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

@Entity
public class Exchange {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;

    @ManyToOne
    private Player requestPlayer;

    @ManyToOne
    private Player targetPlayer;

    @ManyToOne
    private Player requestPlayerContact;

    @ManyToOne
    private Player targetPlayerContact;

    private boolean accepted;

    private boolean completed;

    public Exchange() {
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer, Player requestPlayerContact) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContact = requestPlayerContact;
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer, Player requestPlayerContact, Player targetPlayerContact) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContact = requestPlayerContact;
        this.targetPlayerContact = targetPlayerContact;
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer, Player requestPlayerContact, Player targetPlayerContact, boolean accepted, boolean completed) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContact = requestPlayerContact;
        this.targetPlayerContact = targetPlayerContact;
        this.accepted = accepted;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Player getRequestPlayer() {
        return requestPlayer;
    }

    public void setRequestPlayer(Player requestPlayer) {
        this.requestPlayer = requestPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public Player getRequestPlayerContact() {
        return requestPlayerContact;
    }

    public void setRequestPlayerContact(Player requestPlayerContact) {
        this.requestPlayerContact = requestPlayerContact;
    }

    public Player getTargetPlayerContact() {
        return targetPlayerContact;
    }

    public void setTargetPlayerContact(Player targetPlayerContact) {
        this.targetPlayerContact = targetPlayerContact;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
