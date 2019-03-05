package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ExchangeService {

    /*
     * @param exchange The exchange to save.
     */
    void saveExchange(Exchange exchange);

    /*
     * @param interacter The player requesting the exchange.
     * @param interactee The player with whom to request the exchange.
     * @return An optional of the exchange from interacter to interactee.
     */
    Optional<Exchange> getExchangeByPlayers(Player interacter, Player interactee);

    /*
     * @param exchange The exchange to accept.
     * @param contact The player about which they are giving secondary evidence.
     * @return The player about which they are receiving secondary evidence.
     */
    Long acceptExchange(Exchange exchange, Long contactId);

    /*
     * @param exchange The exchange to complete.
     * @return The player about which they are receiving secondary evidence.
     */
    Long completeExchange(Exchange exchange);

    /*
     * @param exchange The exchange to accept.
     * @param contact The player about which they are giving secondary evidence.
     */
    void resetExchange(Exchange exchange, Long contactId);

    /*
     * @param interacter The player requesting the exchange.
     * @param interactee The player with whom to request the exchange.
     * @param contact The player about which they are giving secondary evidence.
     */
    void createExchange(Player interacter, Player interactee, Long contactId);

    /*
     * @param exchange The exchange.
     * @return True if the exchange is expired.
     */
    boolean isExpired(Exchange exchange);

    /*
     * @param exchange The exchange.
     * @return True if the exchange is active (incomplete and not expired)
     */
    boolean isActive(Exchange exchange);

    /*
     * @param interacter The player requesting the exchange.
     * @param interactee The player with whom to request the exchange.
     * @return True if there exists an active exchange from interacter to interactee
     */
    boolean existsActiveExchangeByPlayers(Player interacter, Player interactee);

    /*
     * Deletes all exchanges in the database
     */
    void deleteAllExchanges();
}
