package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
public class Mission {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotNull
    private Long player1;

    @NotNull
    private Long player2;

    private int Beacon;

    private Boolean sent;

    public Mission() {
        this.startTime = null;
        this.endTime = null;
        this.player1 = null;
        this.player2 = null;
        this.sent = false;
    }

    public Mission(@NotNull LocalTime startTime, @NotNull LocalTime endTime, @NotNull Long player1, @NotNull Long player2) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.player1 = player1;
        this.player2 = player2;
        this.sent = false;
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

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Long getPlayer1() { return player1; }

    public void setPlayer1(Long player1) { this.player1 = player1; }

    public Long getPlayer2() { return player2; }

    public void setPlayer2(Long player2) { this.player2 = player2; }

    public int getBeacon() { return Beacon; }

    public void setBeacon(int beacon) { Beacon = beacon; }

    public Boolean getSent() { return sent; }

    public void setSent(Boolean sent) { this.sent = sent; }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", Beacon=" + Beacon +
                ", sent=" + sent +
                '}';
    }
}
