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

    private Long requestPlayerContactId;

    private Long targetPlayerContactId;

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

    public Exchange(Player requestPlayer, Player targetPlayer, Long requestPlayerContactId) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContactId = requestPlayerContactId;
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer, Long requestPlayerContactId, Long targetPlayerContactId) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContactId = requestPlayerContactId;
        this.targetPlayerContactId = targetPlayerContactId;
        this.accepted = false;
        this.completed = false;
    }

    public Exchange(Player requestPlayer, Player targetPlayer, Long requestPlayerContactId, Long targetPlayerContactId, boolean accepted, boolean completed) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.targetPlayer = targetPlayer;
        this.requestPlayerContactId = requestPlayerContactId;
        this.targetPlayerContactId = targetPlayerContactId;
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

    public Long getRequestPlayerContactId() {
        return requestPlayerContactId;
    }

    public void setRequestPlayerContact(Long requestPlayerContactId) {
        this.requestPlayerContactId = requestPlayerContactId;
    }

    public Long getTargetPlayerContactId() {
        return targetPlayerContactId;
    }

    public void setTargetPlayerContactId(Long targetPlayerContactId) {
        this.targetPlayerContactId = targetPlayerContactId;
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
