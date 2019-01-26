package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Long acceptExchange(Exchange exchange) {
        exchange.setAccepted(true);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getRequestPlayerContact() != null) {
            secondaryId = exchange.getRequestPlayerContact().getId();
        }
        return secondaryId;
    }

    @Override
    public Long completeExchange(Exchange exchange, Player targetPlayerContact) {
        exchange.setCompleted(true);
        exchange.setTargetPlayerContact(targetPlayerContact);
        saveExchange(exchange);
        Long secondaryId = 0l;
        if (exchange.getTargetPlayerContact() != null) {
            secondaryId = exchange.getTargetPlayerContact().getId();
        }
        return secondaryId;
    }

}
