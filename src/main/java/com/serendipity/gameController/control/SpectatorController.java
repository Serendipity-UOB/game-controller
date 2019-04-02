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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

@Controller
@CrossOrigin
public class SpectatorController {

    @Autowired
    LogService logService;

    @GetMapping(value="/spectator")
    public ResponseEntity<String> spectator() {
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        JSONArray logs = logService.logOutput();
        JSONArray zones = logService.zoneDisplay();
        JSONArray leaders = logService.topPlayers();
        JSONObject time = logService.timeRemaining();

        output.put("time", time);
        output.put("logs", logs);
        output.put("leaderboard", leaders);
        output.put("zones", zones);

        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}
