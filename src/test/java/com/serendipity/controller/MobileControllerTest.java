package com.serendipity.controller;

import com.serendipity.gameController.GameControllerApplication;
import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GameControllerApplication.class)
@AutoConfigureMockMvc
public class MobileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    ExchangeServiceImpl exchangeService;

    @Test
    public void newExchangeOneContactTest() throws Exception {
        // Given players
        Player p1 = new Player("Tilly","Headshot");
        Player p2 = new Player("Tom","Cutiekitten");
        Player p3 = new Player("Louis","Puppylover");
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);
        playerService.savePlayer(p3);

        // JSON input
        JSONObject input = new JSONObject();
        input.put("interacter_id",1);
        input.put("interactee_id",2);
        JSONObject contact = new JSONObject();
        contact.put("contact_id",3);
        JSONArray contacts = new JSONArray();
        contacts.put(contact);
        input.put("contact_ids", contacts);

        // Request exchange
        mockMvc.perform(post("/exchange").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
            .andDo(print())
            .andExpect(status().isCreated());
    }

    @Test
    public void newExchangeNoContactsTest() throws Exception {
        // Given players
        Player p1 = new Player("Tilly","Headshot");
        Player p2 = new Player("Tom","Cutiekitten");
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);

        // JSON input
        JSONObject input = new JSONObject();
        input.put("interacter_id",1);
        input.put("interactee_id",2);
        JSONArray contacts = new JSONArray();
        input.put("contact_ids", contacts);

        // Request exchange
        mockMvc.perform(post("/exchange").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void pollOngoingExchangeTest() throws Exception {
        // Given players and exchange
        Player p1 = new Player("Tilly","Headshot");
        Player p2 = new Player("Tom","Cutiekitten");
        Player p3 = new Player("Louis","Puppylover");
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);
        playerService.savePlayer(p3);
        Exchange exchange = new Exchange(p1, p2, p3.getId());
        exchangeService.saveExchange(exchange);

        // JSON input
        JSONObject input = new JSONObject();
        input.put("interacter_id",1);
        input.put("interactee_id",2);
        JSONObject contact = new JSONObject();
        contact.put("contact_id",3);
        JSONArray contacts = new JSONArray();
        contacts.put(contact);
        input.put("contact_ids", contacts);

        // Request exchange
        mockMvc.perform(post("/exchange").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
            .andDo(print())
            .andExpect(status().isAccepted());
    }

    @Test
    public void pollAcceptedExchangeText() throws Exception {
        // Given players and exchange
        Player p1 = new Player("Tilly","Headshot");
        Player p2 = new Player("Tom","Cutiekitten");
        Player p3 = new Player("Louis","Puppylover");
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);
        playerService.savePlayer(p3);
        Exchange exchange = new Exchange(p1, p2, p3.getId(), p3.getId(), true, false);
        exchangeService.saveExchange(exchange);

        // JSON input
        JSONObject input = new JSONObject();
        input.put("interacter_id",1);
        input.put("interactee_id",2);
        JSONObject contact = new JSONObject();
        contact.put("contact_id",3);
        JSONArray contacts = new JSONArray();
        contacts.put(contact);
        input.put("contact_ids", contacts);

        // Request exchange
        JSONObject expected = new JSONObject();
        expected.put("secondary_id",3);
        mockMvc.perform(post("/exchange").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected.toString()));
    }

    @Test
    public void respondExchangeOneContactTest() throws Exception {
        // Given players and exchange
        Player p1 = new Player("Tilly","Headshot");
        Player p2 = new Player("Tom","Cutiekitten");
        Player p3 = new Player("Louis","Puppylover");
        playerService.savePlayer(p1);
        playerService.savePlayer(p2);
        playerService.savePlayer(p3);
        Exchange exchange = new Exchange(p1, p2, p3.getId());
        exchangeService.saveExchange(exchange);

        // JSON input
        JSONObject input = new JSONObject();
        input.put("interacter_id",2);
        input.put("interactee_id",1);
        JSONObject contact = new JSONObject();
        contact.put("contact_id",3);
        JSONArray contacts = new JSONArray();
        contacts.put(contact);
        input.put("contact_ids", contacts);

        // Request exchange
        JSONObject expected = new JSONObject();
        expected.put("secondary_id",3);
        mockMvc.perform(post("/exchange").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expected.toString()));
    }

    @Test
    public void respondExchangeNoContactsTest() throws Exception {

    }

}
