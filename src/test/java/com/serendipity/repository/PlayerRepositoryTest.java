package com.serendipity.repository;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.serendipity.gameController.GameControllerApplication;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameControllerApplication.class)
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void savePlayerTest() {
        Player player = new Player("Tilly","Headshot");
        playerRepository.save(player);
        Assert.assertNotNull(playerRepository.findById(player.getId()));
    }

}
