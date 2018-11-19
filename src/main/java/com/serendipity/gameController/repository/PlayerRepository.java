package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {

}
