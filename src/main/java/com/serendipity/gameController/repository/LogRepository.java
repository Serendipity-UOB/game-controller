package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Log;
import com.serendipity.gameController.model.Zone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends CrudRepository<Log, Long> {

    List<Log> findAllBySent(boolean sent);

    List<Log> findAllByZone(Zone zone);

}
