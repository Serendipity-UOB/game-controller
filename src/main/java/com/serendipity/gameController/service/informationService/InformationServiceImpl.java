package com.serendipity.gameController.service.informationService;

import com.serendipity.gameController.model.Information;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.InformationRepository;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service("informationService")
public class InformationServiceImpl implements InformationService {

    @Autowired
    InformationRepository informationRepository;

    @Autowired
    PlayerServiceImpl playerService;

    @Override
    public void saveInformation(Information information) {
        informationRepository.save(information);
    }

    @Override
    public Optional<Information> getInformation(Long id) {
        return informationRepository.findById(id);
    }

    @Override
    public List<Information> getAllInformationForOwner(Player owner) {
        return informationRepository.findInformationsByOwnerOrderById(owner);
    }

    @Override
    public Optional<Information> getInformationForOwnerAndContact(Player owner, Player contact) {
        return informationRepository.findInformationByOwnerAndContact(owner, contact);
    }

    @Override
    public void incInteractions(Information information) {
        information.setInteractions(information.getInteractions() + 1);
        informationRepository.save(information);
    }

    @Override
    public void deleteAll() {
        informationRepository.deleteAll();
    }

    @Override
    public void initInformation() {
        for (Player player : playerService.getAllPlayers()) {
            for (Player otherPlayer : playerService.getAllPlayersExcept(player)) {
                //Add an information entry
                Information information = new Information(player, otherPlayer, 0);
                saveInformation(information);
            }
        }
    }
}
