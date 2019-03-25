package com.serendipity.gameController.control;

import com.serendipity.gameController.service.logService.LogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class SpectatorController {

    @Autowired
    LogService logService;

    @GetMapping(value="/spectator")
    public ResponseEntity<String> hello() {
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        JSONArray logs = logService.logOutput();
        JSONArray zones = logService.zoneDisplay();
        JSONArray leaders = logService.topPlayers();

        output.put("logs", logs);
        output.put("leaderboard", leaders);
        output.put("zones", zones);

        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}