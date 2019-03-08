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

    public Zone() {
        this.name = "";
        this.beacons = new ArrayList<>();
    }

    public Zone(String name) {
        this.name = name;
        this.beacons = new ArrayList<>();
    }

    public Zone(String name, List<Beacon> beacons) {
        this.name = name;
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

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public void setBeacons(List<Beacon> beacons) {
        this.beacons = beacons;
    }

}
