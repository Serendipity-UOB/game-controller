package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Zone;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends CrudRepository<Zone, Long> {

    List<Zone> findAll();

    List<Zone> findAllByIdNot(Long id);

    List<Zone> findAllByNameNot(String name);
}
