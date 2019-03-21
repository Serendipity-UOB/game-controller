package com.serendipity.gameController.service.exposeService;

import com.serendipity.gameController.model.Expose;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.ExposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void deleteAllExposes() {
        exposeRepository.deleteAll();
    }

}
