package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String realName;

    @NotNull
    private String codeName;

    @ManyToOne
    private Zone homeZone;

    @ManyToOne
    private Player target;

    private int reputation;

    private Long exposedBy;

    private boolean returnHome;

    @ManyToOne
    private Zone currentZone;

    private LocalTime timeEnteredZone;

    @javax.persistence.OrderBy("added ASC")
    @OneToMany(mappedBy = "player")
    private List<PrevZone> prevZones;

    @OneToOne
    private Mission missionAssigned;

    private boolean missionsPaused;

    public Player() {
        this.reputation = 0;
        this.exposedBy = 0l;
        this.returnHome = false;
        this.missionsPaused = false;
        this.prevZones = new ArrayList<>();
    }

    public Player(@NotNull String realName, @NotNull String codeName) {
        this.realName = realName;
        this.codeName = codeName;
        this.reputation = 0;
        this.exposedBy = 0l;
        this.returnHome = false;
        this.missionsPaused = false;
        this.prevZones = new ArrayList<>();
    }

    public Player(@NotNull String realName, @NotNull String codeName, Player target) {
        this.realName = realName;
        this.codeName = codeName;
        this.target = target;
        this.reputation = 0;
        this.exposedBy = 0l;
        this.returnHome = false;
        this.missionsPaused = false;
        this.prevZones = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getCodeName() { return codeName; }

    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }

    public Zone getHomeZone() {
        return homeZone;
    }

    public boolean hasHomeZone() {
        if (this.homeZone == null) return false;
        else return true;
    }

    public void setHomeZone(Zone homeZone) {
        this.homeZone = homeZone;
    }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public Long getExposedBy() { return exposedBy; }

    public void setExposedBy(Long exposedBy) { this.exposedBy = exposedBy; }

    public boolean isReturnHome() { return returnHome; }

    public void setReturnHome(boolean returnHome) { this.returnHome = returnHome; }

    public Zone getCurrentZone() { return currentZone; }

    public void setCurrentZone(Zone currentZone) {
        this.currentZone = currentZone;
    }

    public boolean hasCurrentZone() {
        if (this.currentZone == null) return false;
        else return true;
    }

    public LocalTime getTimeEnteredZone() { return timeEnteredZone; }

    public void setTimeEnteredZone(LocalTime timeEnteredZone) { this.timeEnteredZone = timeEnteredZone; }

    public List<PrevZone> getPrevZones() { return prevZones; }

    public void setPrevZones(List<PrevZone> prevZones) { this.prevZones = prevZones; }

    public Mission getMissionAssigned() { return missionAssigned; }

    public void setMissionAssigned(Mission missionAssigned) { this.missionAssigned = missionAssigned; }

    public boolean isMissionsPaused() { return missionsPaused; }

    public void setMissionsPaused( boolean missionsPaused ) { this.missionsPaused = missionsPaused; }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", codeName='" + codeName + '\'' +
                ", homeZone=" + homeZone +
                ", target=" + target +
                ", reputation=" + reputation +
                ", exposedBy=" + exposedBy +
                ", returnHome=" + returnHome +
                ", currentZone=" + currentZone +
                ", timeEnteredZone=" + timeEnteredZone +
                ", missionAssigned=" + missionAssigned +
                ", missionsPaused=" + missionsPaused +
                '}';
    }
}
