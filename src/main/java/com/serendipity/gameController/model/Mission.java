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
    @ManyToOne
    private Player player;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @ManyToOne
    private Player target1;

    @ManyToOne
    private Player target2;

    @ManyToOne
    private Zone zone;

    private boolean completed;

    public Mission() {
    }

    public Mission(@NotNull Player player, @NotNull LocalTime startTime, @NotNull LocalTime endTime, Player target1, Player target2) {
        this.player = player;
        this.startTime = startTime;
        this.endTime = endTime;
        this.target1 = target1;
        this.target2 = target2;
        this.completed = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

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

    public Player getTarget1() { return target1; }

    public void setTarget1(Player target1) { this.target1 = target1; }

    public Player getTarget2() { return target2; }

    public void setTarget2(Player target2) { this.target2 = target2; }

    public Zone getZone() { return zone; }

    public void setZone(Zone zone) { this.zone = zone; }

    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + id +
                ", player=" + player +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", target1=" + target1 +
                ", target2=" + target2 +
                ", zone=" + zone +
                ", completed=" + completed +
                '}';
    }
}
