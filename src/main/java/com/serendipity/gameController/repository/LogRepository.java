package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends CrudRepository<Log, Long> {

    List<Log> findAllBySent(boolean sent);

}
