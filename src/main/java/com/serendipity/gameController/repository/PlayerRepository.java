package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAllByOrderByReputationDesc();

    Optional<Player> findByCodeName(String codeName);

    List<Player> findAllByNearestBeaconMajor(int nearestBeaconMajor);

    List<Player> findAllByCodeNameNot(String codename);

    List<Player> findAllByCodeNameNotAndCodeNameNot(String codename1, String codename2);

}
