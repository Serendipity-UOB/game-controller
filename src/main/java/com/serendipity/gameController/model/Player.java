package com.serendipity.gameController.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String realName;

    @NotNull
    private String hackerName;

    private int homeBeacon;

    @ManyToOne
    private Player target;

    private int kills;

    private boolean takenDown;

    private boolean returnHome;

    public Player() {
        this.kills = 0;
        this.homeBeacon = -1;
        this.takenDown = false;
        this.returnHome = false;
    }

    public Player(@NotNull String realName, @NotNull String hackerName) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.kills = 0;
        this.takenDown = false;
        this.returnHome = false;
    }

    public Player(@NotNull String realName, @NotNull String hackerName, Player target) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.target = target;
        this.kills = 0;
        this.takenDown = false;
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

    public String getHackerName() {
        return hackerName;
    }

    public void setHackerName(String hackerName) {
        this.hackerName = hackerName;
    }

    public int getHomeBeacon() { return homeBeacon; }

    public void setHomeBeacon(int homeBeacon) { this.homeBeacon = homeBeacon; }

    public Player getTarget() {
        return target;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public boolean isTakenDown() { return takenDown; }

    public void setTakenDown(boolean takenDown) { this.takenDown = takenDown; }

    public boolean isReturnHome() { return returnHome; }

    public void setReturnHome(boolean returnHome) { this.returnHome = returnHome; }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", hackerName='" + hackerName + '\'' +
                ", homeBeacon=" + homeBeacon +
                ", target=" + target +
                ", kills=" + kills +
                ", takenDown=" + takenDown +
                ", returnHome=" + returnHome +
                '}';
    }
}
