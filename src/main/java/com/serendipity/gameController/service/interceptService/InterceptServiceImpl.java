package com.serendipity.gameController.service.interceptService;

import com.serendipity.gameController.model.Intercept;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.InterceptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("interceptService")
public class InterceptServiceImpl implements InterceptService {

    @Autowired
    InterceptRepository interceptRepository;

    @Override
    public void saveIntercept(Intercept intercept){
        interceptRepository.save(intercept);
    }

    @Override
    public void deleteAllIntercepts() {
        interceptRepository.deleteAll();
    }

    @Override
    public Optional<Intercept> getIntercept(Long id){
        return interceptRepository.findById(id);
    }

    @Override
    public Optional<Intercept> getInterceptByPlayer(Player player){ return interceptRepository.findByIntercepter(player); }
}
