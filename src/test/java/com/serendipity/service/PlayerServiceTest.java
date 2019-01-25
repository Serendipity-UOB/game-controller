package com.serendipity.service;

import com.serendipity.gameController.GameControllerApplication;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameControllerApplication.class)
public class PlayerServiceTest {

    @Autowired
    PlayerServiceImpl playerService;

    @Test
    public void savePlayerTest() {
        Player player = new Player("Tilly","Headshot");
        playerService.savePlayer(player);
        Assert.assertNotNull(playerService.getPlayer(player.getId()));
    }
}
