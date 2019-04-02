package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Expose;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExposeRepository extends CrudRepository<Expose, Long> {

}
