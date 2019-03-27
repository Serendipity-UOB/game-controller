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

    public Game() {
        LocalTime startTime = LocalTime.now();
        if (startTime.getSecond() == 0) startTime = startTime.plusSeconds(1);
        this.startTime = startTime;
        this.endTime = this.startTime.plus(6, ChronoUnit.MINUTES);
    }

    public Game(LocalTime startTime) {
        if (startTime.getSecond() == 0) startTime = startTime.plusSeconds(1);
        this.startTime = startTime;
        this.endTime = startTime.plus(6, ChronoUnit.MINUTES);
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
}
