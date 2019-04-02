package com.serendipity.gameController.service.exposeService;

import com.serendipity.gameController.model.Expose;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("exposeService")
public class ExposeServiceImpl implements ExposeService {

    @Autowired
    ExposeRepository exposeRepository;

    @Override
    public void saveExpose(Expose expose){ exposeRepository.save(expose); }

    @Override
    public Optional<Expose> getExpose(Long id){
        return exposeRepository.findById(id);
    }

    @Override
    public List<Expose> getAllExposes() {
        List<Expose> es = new ArrayList<>();
        exposeRepository.findAll().forEach(es::add);
        return es;
    }

    @Override
    public void deleteAllExposes() {
        exposeRepository.deleteAll();
    }

    @Override
    public void unassignPlayers() {
        List<Expose> exposes = getAllExposes();
        for(Expose e: exposes){
            e.setPlayer(null);
            e.setTarget(null);
            saveExpose(e);
        }
    }

}
