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

    @NotNull
    private Long nfcId;

    private int homeBeacon;

    @ManyToOne
    private Player target;

    private int kills;

    public Player() {
        this.kills = 0;
        this.homeBeacon = -1;
    }

    public Player(@NotNull String realName, @NotNull String hackerName) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.nfcId = Long.valueOf(0);
        this.kills = 0;
    }

    public Player(@NotNull String realName, @NotNull String hackerName, @NotNull Long nfcId) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.nfcId = nfcId;
        this.kills = 0;
    }

    public Player(@NotNull String realName, @NotNull String hackerName, Player target) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.nfcId = Long.valueOf(0);
        this.target = target;
        this.kills = 0;
    }

    public Player(@NotNull String realName, @NotNull String hackerName, @NotNull Long nfcId, Player target) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.homeBeacon = -1;
        this.nfcId = nfcId;
        this.target = target;
        this.kills = 0;
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

    public Long getNfcId() { return nfcId; }

    public void setNfcId(Long nfcId) { this.nfcId = nfcId; }

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

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", realName='" + realName + '\'' +
                ", hackerName='" + hackerName + '\'' +
                ", nfcId=" + nfcId +
                ", homeBeacon=" + homeBeacon +
                ", target=" + target.getHackerName() +
                ", kills=" + kills +
                '}';
    }
}
