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
     * @return The id of the exchange
     */
    long createExchange(Player requester, Player responder, JSONArray jsonContactIds);

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
     *
     */
    Optional<Exchange> getNextExchangeToPlayer(Player responder);

    /*
     * @param exchange The exchange you are calculating evidence for.
     * @param player The player giving evidence about themselves.
     * @param contactIds The list of their contacts.
     * @return A list containing evidence objects to be saved to an exchange.
     */
    List<Evidence> calculateEvidence(Exchange exchange, Player player, List<Long> contactIds);

    /*
     * @param exchange The exchange to add evidence to.
     * @param evidenceList The list of evidence to add.
     */
    void addEvidence(Exchange exchange, List<Evidence> evidenceList);

    /*
     * @param player The player to give the evidence to.
     * @return A list of evidence that this player didn't author.
     */
    List<Evidence> getMyEvidence(Exchange exchange, Player player);

    /*
     * Deletes all exchanges in the database
     */
    void deleteAllExchanges();

}
