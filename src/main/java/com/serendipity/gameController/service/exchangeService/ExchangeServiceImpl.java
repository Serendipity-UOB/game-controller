package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.ExchangeResponse;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.time.temporal.ChronoUnit.SECONDS;

@Service("exchangeService")
public class ExchangeServiceImpl implements ExchangeService {

    @Autowired
    ExchangeRepository exchangeRepository;

    @Autowired
    PlayerServiceImpl playerService;

    @Override
    public void saveExchange(Exchange exchange) {
        exchangeRepository.save(exchange);
    }

    @Override
    public Optional<Exchange> getExchange(Long id) { return exchangeRepository.findById(id); }

    @Override
    public long createExchange(Player requester, Player responder, JSONArray jsonContactIds) {
        Exchange exchange = new Exchange(requester, responder);
        List<Long> contactIds = new ArrayList<>();
        if (jsonContactIds.length() > 0) {
            for (int i = 0; i < jsonContactIds.length(); i++) {
                Long id = jsonContactIds.getJSONObject(i).getLong("contact_id");
                contactIds.add(id);
            }
        }
        List<Evidence> evidenceList = calculateEvidence(exchange, requester, contactIds);
        exchange.setEvidenceList(evidenceList);
        saveExchange(exchange);
        return exchange.getId();
    }

    @Override
    public long getTimeRemaining(Exchange exchange) {
        long timeOutPeriod = 10l;
        long timeRemaining;
        if (exchange.getStartTime().plusSeconds(timeOutPeriod).isBefore(LocalTime.now())) {
            timeRemaining = 0l;
        } else {
            timeRemaining = SECONDS.between(LocalTime.now(), exchange.getStartTime().plusSeconds(timeOutPeriod));
        }
        return timeRemaining;
    }

    @Override
    public Optional<Exchange> getExchangeByPlayers(Player requester, Player responder) {
        List<Exchange> exchangeList = exchangeRepository.findAllByRequestPlayerAndResponsePlayerOrderByStartTimeDesc(requester, responder);
        if (exchangeList.size() > 0) return  Optional.of(exchangeList.get(0));
        else return Optional.empty();
    }

    @Override
    public Optional<Exchange> getMostRecentExchangeFromPlayer(Player requester) {
        List<Exchange> exchangeList = exchangeRepository.findAllByRequestPlayerOrderByStartTimeDesc(requester);
        if (exchangeList.size() > 0) return Optional.of(exchangeList.get(0));
        else return Optional.empty();
    }

    @Override
    public Optional<Exchange> getMostRecentExchangeToPlayer(Player responder) {
        List<Exchange> exchangeList = exchangeRepository.findAllByResponsePlayerOrderByStartTimeDesc(responder);
        if (exchangeList.size() > 0) return Optional.of(exchangeList.get(0));
        else return Optional.empty();
    }

    @Override
    public Optional<Exchange> getNextExchangeToPlayer(Player responder) {
        Optional<Exchange> optionalExchange = Optional.empty();
        // Set response to empty to begin
        // Get the most recent exchange that the player has been told about
        // Get all exchanges by: responsePlayer, and requestSent==true, orderByStartTimeDesc
        List<Exchange> oldExchanges = exchangeRepository.findAllByResponsePlayerAndRequestSentOrderByStartTimeDesc(responder, true);
        // Get first from list if exists
        if (oldExchanges.size() > 0) {
            System.out.println("Found most recent exchange told about");
            Exchange oldExchange = oldExchanges.get(0);
            // Is response ACCEPTED or REJECTED?
            if ((!oldExchange.getResponse().equals(ExchangeResponse.WAITING)) || (getTimeRemaining(oldExchange) <= 0l)) {
                System.out.println("Most recent exchange is responded to or completed");
                // If yes, get the next exchange
                // Get all exchanges by: responsePlayer, and requestSent==false, and startTime > now.plusSeconds(), orderByStartTimeAsc
                List<Exchange> newExchanges = exchangeRepository.findAllByResponsePlayerAndRequestSentAndStartTimeAfterOrderByStartTimeAsc(
                        responder, false, LocalTime.now().minus(9, SECONDS));
                // Get first from list if exists
                if (newExchanges.size() > 0) {
                    System.out.println("Found exchange waiting");
                        Exchange newExchange = newExchanges.get(0);
                    // Wrap it in an optional
                    optionalExchange = Optional.of(newExchange);
                }
            }
        } else {
            // Get all exchanges by: responsePlayer, and requestSent==false, and startTime > now.plusSeconds(), orderByStartTimeAsc
            List<Exchange> newExchanges = exchangeRepository.findAllByResponsePlayerAndRequestSentAndStartTimeAfterOrderByStartTimeAsc(
                    responder, false, LocalTime.now().minus(9, SECONDS));
            // Get first from list if exists
            if (newExchanges.size() > 0) {
                Exchange newExchange = newExchanges.get(0);
                // Wrap it in an optional
                optionalExchange = Optional.of(newExchange);
            }
        }
        return optionalExchange;
    }

    @Override
    public Optional<Exchange> getEarliestActiveExchange(Player player) {
        List<Exchange> exchangeList = exchangeRepository.findAllByRequestPlayerAndRequesterToldCompleteOrResponsePlayerAndRequesterToldCompleteOrderByStartTimeAsc(player, false, player, false);
        if (exchangeList.size() > 0) return Optional.of(exchangeList.get(0));
        else return Optional.empty();
    }

    @Override
    public List<Evidence> calculateEvidence(Exchange exchange, Player player, List<Long> contactIds) {
        List<Evidence> evidenceList = new ArrayList<>();

        // Make evidence on the player giving evidence
        Evidence evidence = new Evidence(exchange, player, player, 10);
        evidenceList.add(evidence);

        // Remove the responder and requester ids from contacts list
        if (contactIds.contains(exchange.getResponsePlayer().getId())) {
            contactIds.remove(exchange.getResponsePlayer().getId());
        }
        if (contactIds.contains(exchange.getRequestPlayer().getId())) {
            contactIds.remove(exchange.getRequestPlayer().getId());
        }

        // Pick randomly from contactIds
        if (contactIds.size() != 0) {
            Random random = new Random();
            Player contact = playerService.getPlayer(contactIds.get(random.nextInt(contactIds.size()))).get();
            evidence = new Evidence(exchange, contact, player, 20);
            evidenceList.add(evidence);
        }

        return evidenceList;
    }

    @Override
    public void addEvidence(Exchange exchange, List<Evidence> newEvidenceList) {
        List<Evidence> existingEvidenceList = exchange.getEvidenceList();
        existingEvidenceList.addAll(newEvidenceList);
        exchange.setEvidenceList(existingEvidenceList);
        saveExchange(exchange);
    }

    @Override
    public List<Evidence> getMyEvidence(Exchange exchange, Player player) {
        List<Evidence> evidenceList = new ArrayList<>();
        List<Evidence> allEvidence = exchange.getEvidenceList();
        for (Evidence evidence : allEvidence) {
            if (!evidence.getAuthor().equals(player)) evidenceList.add(evidence);
        }
        return evidenceList;
    }

    @Override
    public void deleteAllExchanges() {
        if (exchangeRepository.count() != 0) {
            exchangeRepository.deleteAll();
        }
    }

}
