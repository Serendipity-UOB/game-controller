package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
public class Beacon {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private int major;

    @NotNull
    private int minor;

    @NotNull
    private String identifier;

    @ManyToOne
    private Zone zone;

    public Beacon() {  }

    public Beacon(@NotNull int major, @NotNull int minor, @NotNull String identifier, Zone zone) {
        this.major = major;
        this.minor = minor;
        this.identifier = identifier;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMajor() { return major; }

    public void setMajor(int major) { this.major = major; }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

}
