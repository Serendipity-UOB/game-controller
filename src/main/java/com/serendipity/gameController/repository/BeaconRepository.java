package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Beacon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeaconRepository extends CrudRepository<Beacon, Long> {

    Optional<Beacon> getBeaconByMinor (int minor);

}
