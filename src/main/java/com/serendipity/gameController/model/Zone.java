package com.serendipity.gameController.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Zone {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Beacon> beacons;

    private float x;

    private float y;

    public Zone() {
        this.name = "";
        this.beacons = new ArrayList<>();
    }

    public Zone(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.beacons = new ArrayList<>();
    }

    public Zone(String name, List<Beacon> beacons, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.beacons = beacons;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getX() { return x; }

    public void setX(int x) { this.x = x; }

    public float getY() { return y; }

    public void setY(int y) { this.y = y; }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

}
