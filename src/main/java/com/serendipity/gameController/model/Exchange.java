package com.serendipity.gameController.model;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Exchange {

    @Id
    @GeneratedValue
    private Long id;

    private LocalTime startTime;

    @ManyToOne
    private Player requestPlayer;

    @ManyToOne
    private Player responsePlayer;

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Evidence> requestEvidence; // The evidence from the requester

    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Evidence> responseEvidence; // The evidence from the responder

    private ExchangeResponse response;

    private boolean timeoutTold;

    public Exchange() {
        this.startTime = LocalTime.now();
        this.response = ExchangeResponse.WAITING;
        this.requestEvidence = new ArrayList<>();
        this.responseEvidence = new ArrayList<>();
        this.timeoutTold = false;
    }

    public Exchange(Player requestPlayer, Player responsePlayer) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.responsePlayer = responsePlayer;
        this.response = ExchangeResponse.WAITING;
        this.requestEvidence = new ArrayList<>();
        this.responseEvidence = new ArrayList<>();
        this.timeoutTold = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Player getRequestPlayer() {
        return requestPlayer;
    }

    public void setRequestPlayer(Player requestPlayer) {
        this.requestPlayer = requestPlayer;
    }

    public Player getResponsePlayer() {
        return responsePlayer;
    }

    public void setResponsePlayer(Player responsePlayer) {
        this.responsePlayer = responsePlayer;
    }


    public List<Evidence> getRequestEvidence() {
        return requestEvidence;
    }

    public void setRequestEvidence(List<Evidence> requestEvidence) {
        this.requestEvidence = requestEvidence;
    }

    public List<Evidence> getResponseEvidence() {
        return responseEvidence;
    }

    public void setResponseEvidence(List<Evidence> responseEvidence) {
        this.responseEvidence = responseEvidence;
    }

    public ExchangeResponse getResponse() {
        return response;
    }

    public void setResponse(ExchangeResponse response) {
        this.response = response;
    }

    public boolean isTimeoutTold() {
        return timeoutTold;
    }

    public void setTimeoutTold(boolean timeoutTold) {
        this.timeoutTold = timeoutTold;
    }

}
