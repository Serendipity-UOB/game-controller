package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAllByOrderByKillsDesc();

    Optional<Player> findByHackerName(String hackerName);

    List<Player> findAllByNearestBeaconMajor(int nearestBeaconMajor);

    List<Player> findAllByHackerNameNot(String hackername);

    List<Player> findAllByHackerNameNotAndHackerNameNot(String hackername1, String hackername2);

}
