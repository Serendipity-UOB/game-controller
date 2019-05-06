package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Game;
import org.apache.tomcat.jni.Local;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAll();

    List<Game> findAllByOrderByStartTimeAsc();

    Optional<Game> findFirstByStartTimeGreaterThanEqualOrderByStartTimeAsc(LocalTime time);

    Optional<Game> findFirstByStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalTime startTime, LocalTime endTime);

}
