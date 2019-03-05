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

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void getAllPlayersExceptTest() {
        Player p1 = playerService.getPlayerByCodeName("A").get();
        Player p2 = playerService.getPlayerByCodeName("B").get();
        Player p3 = playerService.getPlayerByCodeName("C").get();
        Player p4 = playerService.getPlayerByCodeName("D").get();
        Player p5 = playerService.getPlayerByCodeName("E").get();
        Player p6 = playerService.getPlayerByCodeName("F").get();
        List<Player> expectedRemaining = new ArrayList<>();
        expectedRemaining.add(p2);
        expectedRemaining.add(p3);
        expectedRemaining.add(p4);
        expectedRemaining.add(p5);
        expectedRemaining.add(p6);
        List<Player> remainingPlayers = playerService.getAllPlayersExcept(p1);
        Assert.assertEquals(expectedRemaining, remainingPlayers);

    }

    @Test
    public void getAllPlayersExceptTwoTest() {
        Player p1 = playerService.getPlayerByCodeName("A").get();
        Player p2 = playerService.getPlayerByCodeName("B").get();
        Player p3 = playerService.getPlayerByCodeName("C").get();
        Player p4 = playerService.getPlayerByCodeName("D").get();
        Player p5 = playerService.getPlayerByCodeName("E").get();
        Player p6 = playerService.getPlayerByCodeName("F").get();
        List<Player> expectedRemaining = new ArrayList<>();
        expectedRemaining.add(p3);
        expectedRemaining.add(p4);
        expectedRemaining.add(p5);
        expectedRemaining.add(p6);
        List<Player> remainingPlayers = playerService.getAllPlayersExceptTwo(p1, p2);
        Assert.assertEquals(expectedRemaining, remainingPlayers);
    }
}
