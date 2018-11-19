package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    private RealName realName;

    private HackerName hackerName;

    @ManyToOne
    private Player target;

    private int kills;

    public Player(RealName realName, HackerName hackerName, Player target) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.target = target;
        this.kills = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RealName getRealName() {
        return realName;
    }

    public void setRealName(RealName realName) {
        this.realName = realName;
    }

    public HackerName getHackerName() {
        return hackerName;
    }

    public void setHackerName(HackerName hackerName) {
        this.hackerName = hackerName;
    }

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
}
