package com.serendipity.gameController.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @Fetch(value = FetchMode.SUBSELECT)
    private List<Evidence> evidenceList; // The evidence from the requester

//    @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Evidence> responseEvidence; // The evidence from the responder

    private ExchangeResponse response;

    private boolean requesterToldComplete;

    private boolean requestSent;

    public Exchange() {
        this.startTime = LocalTime.now();
        this.response = ExchangeResponse.WAITING;
        this.evidenceList = new ArrayList<>();
//        this.responseEvidence = new ArrayList<>();
        this.requesterToldComplete = false;
        this.requestSent = false;
    }

    public Exchange(Player requestPlayer, Player responsePlayer) {
        this.startTime = LocalTime.now();
        this.requestPlayer = requestPlayer;
        this.responsePlayer = responsePlayer;
        this.response = ExchangeResponse.WAITING;
        this.evidenceList = new ArrayList<>();
//        this.responseEvidence = new ArrayList<>();
        this.requesterToldComplete = false;
        this.requestSent = false;
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


    public List<Evidence> getEvidenceList() {
        return evidenceList;
    }

    public void setEvidenceList(List<Evidence> requestEvidence) {
        this.evidenceList = requestEvidence;
    }

//    public List<Evidence> getResponseEvidence() {
//        return responseEvidence;
//    }
//
//    public void setResponseEvidence(List<Evidence> responseEvidence) {
//        this.responseEvidence = responseEvidence;
//    }

    public ExchangeResponse getResponse() {
        return response;
    }

    public void setResponse(ExchangeResponse response) {
        this.response = response;
    }

    public boolean isRequesterToldComplete() {
        return requesterToldComplete;
    }

    public void setRequesterToldComplete(boolean requesterToldComplete) {
        this.requesterToldComplete = requesterToldComplete;
    }

    public boolean isRequestSent() {
        return requestSent;
    }

    public void setRequestSent(boolean requestSent) {
        this.requestSent = requestSent;
    }
}
