package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends CrudRepository<Exchange, Long> {

    Optional<Exchange> findExchangeByRequestPlayerAndResponsePlayer(Player requestPlayer, Player targetPlayer);

    List<Exchange> findAllByRequestPlayerOrderByStartTimeDesc(Player requestPlayer);

    List<Exchange> findAllByResponsePlayerOrderByStartTimeDesc(Player responsePlayer);

}
