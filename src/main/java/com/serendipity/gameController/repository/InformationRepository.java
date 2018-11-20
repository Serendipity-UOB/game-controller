package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Information;
import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformationRepository extends CrudRepository<Information, Long> {

    List<Information> findInformationsByOwner(Player owner);

}
