package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Evidence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvidenceRepository extends CrudRepository<Evidence, Long> {

}
