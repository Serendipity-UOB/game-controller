package com.serendipity.gameController.service.exchangeService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ExchangeService {

    /*
     * @param exchange The exchange to save.
     */
    void saveExchange(Exchange exchange);

    /*
     *
     */
    void createExchange(Player requester, Player responder, JSONArray jsonContactIds);

    /*
     * @param exchange The exchange you're looking at.
     * @return The time remaining until exchange timeout.
     */
    long getTimeRemaining(Exchange exchange);

    /*
     * @param requester The player requesting the exchange.
     * @param responder The player with whom to request the exchange.
     * @return An optional of the exchange from requester to responder.
     */
    Optional<Exchange> getExchangeByPlayers(Player requester, Player responder);

    /*
     * @param requester The player you want an exchange from.
     * @return An optional of the active exchange from that player.
     */
    Optional<Exchange> getMostRecentExchangeFromPlayer(Player requester);

    /*
     * @param responder The player you want an exchange to.
     * @return An optional of the active exchange to that player.
     */
    Optional<Exchange> getMostRecentExchangeToPlayer(Player responder);

    /*
     * @param exchange The exchange you are calculating evidence for.
     * @param player The player giving evidence about themselves.
     * @param contactIds The list of their contacts.
     * @return A list containing evidence objects to be saved to an exchange.
     */
    List<Evidence> calculateEvidence(Exchange exchange, Player player, List<Long> contactIds);

    /*
     * Deletes all exchanges in the database
     */
    void deleteAllExchanges();

    /*
     * @param interacter The player requesting the exchange.
     * @param interactee The player with whom to request the exchange.
     * @param contact The player about which they are giving secondary evidence.
     */
//    void createExchange(Player interacter, Player interactee, Long contactId);

//    /*
//     * @param exchange The exchange.
//     * @return True if the exchange is expired.
//     */
//    boolean isExpired(Exchange exchange);
//
//    /*
//     * @param exchange The exchange.
//     * @return True if the exchange is active (incomplete and not expired)
//     */
//    boolean isActive(Exchange exchange);

    /*
     * @param interacter The player requesting the exchange.
     * @param interactee The player with whom to request the exchange.
     * @return True if there exists an active exchange from interacter to interactee
     */
//    boolean existsActiveExchangeByPlayers(Player interacter, Player interactee);

}
