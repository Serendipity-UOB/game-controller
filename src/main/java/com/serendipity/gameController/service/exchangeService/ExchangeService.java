package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ExchangeService {

    void saveExchange(Exchange exchange);

    Optional<Exchange> getExchangeByPlayers(Player interacter, Player interactee);

    Long acceptExchange(Exchange exchange, Player targetPlayerContact);

    Long completeExchange(Exchange exchange);

    void resetExchange(Exchange exchange, Player requestPlayerContact);

    void createExchange(Player interacter, Player interactee, Player contact);

    boolean isExpired(Exchange exchange);

    boolean isActive(Exchange exchange);

    boolean existsActiveExchangeByPlayers(Player interacter, Player interactee);

}
