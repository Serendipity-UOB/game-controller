package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Mission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends CrudRepository<Mission, Long> {

}
