package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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

}
