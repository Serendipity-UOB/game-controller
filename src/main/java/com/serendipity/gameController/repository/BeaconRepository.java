package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Beacon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeaconRepository extends CrudRepository<Beacon, Long> {

    List<Beacon> findAllByMajor (int major);

    List<Beacon> findAll();

    void deleteBeaconById(long id);

}
