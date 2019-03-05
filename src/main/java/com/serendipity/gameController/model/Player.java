package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String realName;

    @NotNull
    private String codeName;

    private int homeBeacon;

    @ManyToOne
    private Player target;

    private int rep;

    private boolean exposed;

    private boolean returnHome;

    private int nearestBeaconMajor;

    public Player() {
        this.rep = 0;
        this.homeBeacon = -1;
        this.exposed = false;
        this.returnHome = false;
    }

    public Player(@NotNull String realName, @NotNull String codeName) {
        this.realName = realName;
        this.codeName = codeName;
        this.homeBeacon = -1;
        this.rep = 0;
        this.exposed = false;
        this.returnHome = false;
    }

    public Player(@NotNull String realName, @NotNull String codeName, Player target) {
        this.realName = realName;
        this.codeName = codeName;
        this.homeBeacon = -1;
        this.target = target;
        this.rep = 0;
        this.exposed = false;
        this.returnHome = false;
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

    public String getCodeName() {
        return codeName;
    }

    public void getCodeName(String codeName) {
        this.codeName = codeName;
    }

    public int getHomeBeacon() { return homeBeacon; }

    public void setHomeBeacon(int homeBeacon) { this.homeBeacon = homeBeacon; }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public boolean isExposed() { return exposed; }

    public void setExposed(boolean exposed) { this.exposed = exposed; }

    public boolean isReturnHome() { return returnHome; }

    public void setReturnHome(boolean returnHome) { this.returnHome = returnHome; }

    public int getNearestBeaconMajor() {
        return nearestBeaconMajor;
    }

    public void setNearestBeaconMajor(int nearestBeaconMajor) {
        this.nearestBeaconMajor = nearestBeaconMajor;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", codeName='" + codeName + '\'' +
                ", homeBeacon=" + homeBeacon +
                ", target=" + target +
                ", rep=" + rep +
                ", exposed=" + exposed +
                ", returnHome=" + returnHome +
                ", nearestBeaconMajor=" + nearestBeaconMajor +
                '}';
    }
}
