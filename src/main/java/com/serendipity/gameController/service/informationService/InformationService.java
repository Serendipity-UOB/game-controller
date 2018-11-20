package com.serendipity.gameController.service.informationService;

import com.serendipity.gameController.model.Information;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface InformationService {

    void saveInformation(Information information);

    Optional<Information> getInformation(Long id);

    List<Information> getAllInformationForOwner(Player owner);

    Optional<Information> getInformationForOwnerAndContact(Player owner, Player contact);

    void incInteractions(Information information);

    void deleteAll();

    void initInformation();

}
