package com.serendipity.gameController.model;

import org.springframework.expression.spel.ast.NullLiteral;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Player {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String realName;

    @NotNull
    private String hackerName;

    @ManyToOne
    private Player target;

    private int kills;

    public Player() {
        this.realName = "";
        this.hackerName = "";
        this.kills = 0;
    }

    public Player(@NotNull String realName, @NotNull String hackerName) {
        this.realName = realName;
        this.hackerName = hackerName;
        this.kills = 0;
    }

    public Player(String realName, String hackerName, Player target) {
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
                ", target=" + target +
                ", kills=" + kills +
                '}';
    }
}
