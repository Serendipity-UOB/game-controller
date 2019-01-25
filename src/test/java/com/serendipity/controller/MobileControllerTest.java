package com.serendipity.controller;

import com.serendipity.gameController.GameControllerApplication;
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

    @Test
    public void getMappingTest() throws Exception {
        mockMvc.perform(get("/getTest"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json("{'aa':'bb'}"));
    }

    @Test
    public void postMappingTest() throws Exception {
        JSONObject input = new JSONObject();
        input.put("aa","bb");
        mockMvc.perform(post("/postTest").contentType(MediaType.APPLICATION_JSON).content(input.toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().json(input.toString()));
    }

}
