package com.serendipity.gameController.repository;

import com.serendipity.gameController.model.Information;
import com.serendipity.gameController.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface InformationRepository extends CrudRepository<Information, Long> {

    List<Information> findInformationsByOwner(Player owner);

    Optional<Information> findInformationByOwnerAndContact(Player owner, Player contact);

}
