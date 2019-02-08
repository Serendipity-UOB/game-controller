package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAll();

    List<Game> findAllByOrderByStartTimeAsc();

    Optional<Game> findFirstByStartTimeAfterOrderByStartTimeAsc(LocalTime time);

}
