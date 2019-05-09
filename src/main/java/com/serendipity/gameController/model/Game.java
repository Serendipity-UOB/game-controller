package com.serendipity.gameController.model;

import org.apache.tomcat.jni.Local;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private int minPlayers;

    public Game() {
        LocalTime startTime = LocalTime.now();
        if (startTime.getSecond() == 0) startTime = startTime.plusSeconds(1);
        this.startTime = startTime;
        this.endTime = this.startTime.plus(6, ChronoUnit.MINUTES);
        this.minPlayers = 3;
    }

    public Game(LocalTime startTime, LocalTime endTime, int minPlayers) {
        if (startTime.getSecond() == 0) startTime = startTime.plusSeconds(1);
        this.startTime = startTime;
        this.endTime = endTime;
        this.minPlayers = minPlayers;
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

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }
}
