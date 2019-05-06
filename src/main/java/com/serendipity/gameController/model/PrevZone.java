package com.serendipity.gameController.model;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
public class PrevZone {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime added;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    public PrevZone() {
    }

    public PrevZone(LocalTime added, Player player, Zone zone) {
        this.added = added;
        this.player = player;
        this.zone = zone;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalTime getAdded() { return added; }

    public void setAdded(LocalTime added) { this.added = added; }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public Zone getZone() { return zone; }

    public void setZone(Zone zone) { this.zone = zone; }
}
