package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.PrevZone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrevZoneRepository extends CrudRepository<PrevZone, Long> {
}
