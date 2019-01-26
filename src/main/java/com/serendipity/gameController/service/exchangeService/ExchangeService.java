package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ExchangeService {

    void saveExchange(Exchange exchange);

    Optional<Exchange> getExchangeByPlayers(Player interacter, Player interactee);

}
