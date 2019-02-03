package com.serendipity.gameController.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private String name;

    public Beacon() {  }

    public Beacon(@NotNull int minor, @NotNull String name) {
        this.minor = minor;
        this.name = name;
    }

    public Beacon(@NotNull int major, @NotNull int minor, @NotNull String name) {
        this.major = major;
        this.minor = minor;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMajor() { return major; }

    public void setMajor(int major) { this.major = major; }
}
