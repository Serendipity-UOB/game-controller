package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Long acceptExchange(Exchange exchange, Long contactId) {
        if (contactId == exchange.getRequestPlayer().getId()) contactId = 0l;
        exchange.setAccepted(true);
        exchange.setTargetPlayerContactId(contactId);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getRequestPlayerContactId() != null) {
            secondaryId = exchange.getRequestPlayerContactId();
        }
        return secondaryId;
    }

    @Override
    public Long completeExchange(Exchange exchange) {
        exchange.setCompleted(true);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getTargetPlayerContactId() != null) {
            secondaryId = exchange.getTargetPlayerContactId();
        }
        return secondaryId;
    }

    @Override
    public void resetExchange(Exchange exchange, Long contactId) {
        if (contactId == exchange.getTargetPlayer().getId()) contactId = 0l;
        exchange.setRequestPlayerContact(contactId);
        exchange.setAccepted(false);
        exchange.setCompleted(false);
        exchange.setStartTime(LocalTime.now());
        saveExchange(exchange);
    }

    @Override
    public void createExchange(Player interacter, Player interactee, Long contactId) {
        Optional<Exchange> exchangeOptional = getExchangeByPlayers(interacter, interactee);
        if (contactId == interactee.getId()) contactId = 0l;
        if (exchangeOptional.isPresent()) {
            Exchange exchange = exchangeOptional.get();
            resetExchange(exchange, contactId);
        } else {
            Exchange exchange = new Exchange(interacter, interactee, contactId);
            saveExchange(exchange);
        }
    }

    @Override
    public boolean isExpired(Exchange exchange) {
        return LocalTime.now().isAfter(exchange.getStartTime().plusSeconds(5));
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

    @Override
    public void deleteAllExchanges() {
        if (exchangeRepository.count() != 0) {
            exchangeRepository.deleteAll();
        }
    }

}
