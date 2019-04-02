package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import org.apache.tomcat.jni.Local;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends CrudRepository<Exchange, Long> {

    List<Exchange> findAllByRequestPlayerAndResponsePlayerOrderByStartTimeDesc(Player requestPlayer, Player responsePlayer);

    List<Exchange> findAllByRequestPlayerOrderByStartTimeDesc(Player requestPlayer);

    List<Exchange> findAllByResponsePlayerOrderByStartTimeDesc(Player responsePlayer);

    List<Exchange> findAllByResponsePlayerAndRequestSentOrderByStartTimeDesc(Player responsePlayer, boolean requestSent);

    List<Exchange> findAllByResponsePlayerAndRequestSentAndStartTimeAfterOrderByStartTimeAsc(Player responsePlayer, LocalTime time, boolean requestSent);

}
