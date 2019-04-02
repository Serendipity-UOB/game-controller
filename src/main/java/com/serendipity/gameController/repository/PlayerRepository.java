package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Mission;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Zone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findAllByOrderByReputationDesc();

    Optional<Player> findByCodeName(String codeName);

    Optional<Player> findByMissionAssigned(Mission mission);

    List<Player> findAllByCurrentZone(Zone zone);

    List<Player> findAllByCodeNameNot(String codename);

    List<Player> findAllByCodeNameNotAndCodeNameNot(String codename1, String codename2);

}
