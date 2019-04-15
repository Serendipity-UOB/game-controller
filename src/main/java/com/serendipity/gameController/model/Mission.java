package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
public class Mission {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne
    private Player player1;

    @ManyToOne
    private Player player2;

    @ManyToOne
    private Zone zone;

    private boolean start;

    private boolean completed;

    public Mission() {
    }

    public Mission(@NotNull Player player1, @NotNull Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.completed = false;
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

    public Player getPlayer1() { return player1; }

    public void setPlayer1(Player player1) { this.player1 = player1; }

    public Player getPlayer2() { return player2; }

    public void setPlayer2(Player player2) { this.player2 = player2; }

    public Zone getZone() { return zone; }

    public void setZone(Zone zone) { this.zone = zone; }

    public boolean isStart() { return start; }

    public void setStart(boolean start) { this.start = start; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", player1=" + player1 +
                ", player2=" + player2 +
                ", zone=" + zone +
                ", start=" + start +
                ", completed=" + completed +
                '}';
    }
}
