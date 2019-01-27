package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Optional;

@Service("exchangeService")
public class ExchangeServiceImpl implements ExchangeService{

    @Autowired
    ExchangeRepository exchangeRepository;

    @Override
    public void saveExchange(Exchange exchange) {
        exchangeRepository.save(exchange);
    }

    @Override
    public Optional<Exchange> getExchangeByPlayers(Player interacter, Player interactee) {
        return exchangeRepository.findExchangeByRequestPlayerAndTargetPlayer(interacter, interactee);
    }

    @Override
    public Long acceptExchange(Exchange exchange, Player targetPlayerContact) {
        exchange.setAccepted(true);
        exchange.setTargetPlayerContact(targetPlayerContact);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getRequestPlayerContact() != null) {
            secondaryId = exchange.getRequestPlayerContact().getId();
        }
        return secondaryId;
    }

    @Override
    public Long completeExchange(Exchange exchange) {
        exchange.setCompleted(true);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getTargetPlayerContact() != null) {
            secondaryId = exchange.getTargetPlayerContact().getId();
        }
        return secondaryId;
    }

    @Override
    public void resetExchange(Exchange exchange, Player requestPlayerContact) {
        exchange.setRequestPlayerContact(requestPlayerContact);
        exchange.setAccepted(false);
        exchange.setCompleted(false);
        exchange.setStartTime(LocalTime.now());
        saveExchange(exchange);
    }

    @Override
    public void createExchange(Player interacter, Player interactee, Player contact) {
        Optional<Exchange> exchangeOptional = getExchangeByPlayers(interacter, interactee);
        if (exchangeOptional.isPresent()) {
            Exchange exchange = exchangeOptional.get();
            resetExchange(exchange, contact);
        } else {
            Exchange exchange = new Exchange(interacter, interactee, contact);
            saveExchange(exchange);
        }
    }

    @Override
    public boolean isExpired(Exchange exchange) {
        return LocalTime.now().isAfter(exchange.getStartTime().plusSeconds(10));
    }

    @Override
    public boolean isActive(Exchange exchange) {
        return !(exchange.isCompleted());
    }

    @Override
    public boolean existsActiveExchangeByPlayers(Player interacter, Player interactee) {
        boolean exists = false;
        Optional<Exchange> exchangeOptional = getExchangeByPlayers(interacter, interactee);
        if (exchangeOptional.isPresent()) {
            Exchange exchange = exchangeOptional.get();
            exists = isActive(exchange);
        }
        return exists;
    }

}
