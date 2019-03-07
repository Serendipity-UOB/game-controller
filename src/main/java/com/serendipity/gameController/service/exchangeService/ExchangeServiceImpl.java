package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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
    public void createExchange(Player requester, Player responder, JSONArray jsonContactIds) {
        Exchange exchange = new Exchange(requester, responder);
        List<Long> contactIds = new ArrayList<>();
        for (int i = 0; i < jsonContactIds.length(); i++) {
            Long id = jsonContactIds.getJSONObject(i).getLong("contact_id");
            contactIds.add(id);
        }
        List<Evidence> evidenceList = calculateEvidence(exchange, requester, contactIds);
        exchange.setRequestEvidence(evidenceList);
        saveExchange(exchange);
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
        return exchangeRepository.findExchangeByRequestPlayerAndResponsePlayer(requester, responder);
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
    public List<Evidence> calculateEvidence(Exchange exchange, Player player, List<Long> contactIds) {
        List<Evidence> evidenceList = new ArrayList<>();

        // Make evidence on the player giving evidence
        Evidence evidence = new Evidence(exchange, player, 10);
        evidenceList.add(evidence);

        // Remove the responder id from contacts list
        // TODO: Test if this works (is equals enough or does it only work on the same actual object?)
        if (contactIds.contains(exchange.getResponsePlayer().getId())) {
            contactIds.remove(exchange.getResponsePlayer().getId());
        }
        if (contactIds.contains(exchange.getRequestPlayer().getId())) {
            contactIds.remove(exchange.getRequestPlayer().getId());
        }

        // Pick randomly from contactIds
        if (contactIds.size() != 0) {
            Random random = new Random();
            // TODO: Validation on optional
            Player contact = playerService.getPlayer(contactIds.get(random.nextInt(contactIds.size()))).get();
            evidence = new Evidence(exchange, contact, 20);
            evidenceList.add(evidence);
        }

        return evidenceList;
    }

    @Override
    public void deleteAllExchanges() {
        if (exchangeRepository.count() != 0) {
            exchangeRepository.deleteAll();
        }
    }

    //    @Override
//    public void createExchange(Player interacter, Player interactee, Long contactId) {
////        Optional<Exchange> exchangeOptional = getExchangeByPlayers(interacter, interactee);
////        if (contactId == interactee.getId()) contactId = 0l;
////        if (exchangeOptional.isPresent()) {
////            Exchange exchange = exchangeOptional.get();
////            resetExchange(exchange, contactId);
////        } else {
////            Exchange exchange = new Exchange(interacter, interactee);
////            // TODO: Set evidence
////            saveExchange(exchange);
////        }
//    }

//    @Override
//    public boolean isExpired(Exchange exchange) {
//        return LocalTime.now().isAfter(exchange.getStartTime().plusSeconds(10));
//    }
//
//    @Override
//    public boolean isActive(Exchange exchange) {
//        // TODO
////        return !(exchange.isCompleted());
//        return true;
//    }

//    @Override
//    public boolean existsActiveExchangeByPlayers(Player interacter, Player interactee) {
//        boolean exists = false;
//        Optional<Exchange> exchangeOptional = getExchangeByPlayers(interacter, interactee);
//        if (exchangeOptional.isPresent()) {
//            Exchange exchange = exchangeOptional.get();
//            exists = isActive(exchange);
//        }
//        return exists;
//    }

}
