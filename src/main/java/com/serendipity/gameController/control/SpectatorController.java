package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.logService.LogService;
import com.serendipity.gameController.service.logService.LogServiceImpl;
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
import java.util.*;

@Controller
@CrossOrigin
public class SpectatorController {

    @Autowired
    LogServiceImpl logService;

    @Autowired
    GameServiceImpl gameService;

    @GetMapping(value="/spectator")
    public ResponseEntity<String> spectator() {
        JSONObject output = new JSONObject();
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        Optional<Game> optionalGame = gameService.getCurrentGame();

        if (optionalGame.isPresent()) {

            // If game is currently going on
            JSONArray logs = logService.logOutput();
            JSONArray zones = logService.zoneDisplay();
            JSONArray leaders = logService.topPlayers();
            JSONObject time = logService.timeRemaining();
            output.put("time", time);
            output.put("logs", logs);
            output.put("leaderboard", leaders);
            output.put("zones", zones);
            responseStatus = HttpStatus.OK;
        } else {
            optionalGame = gameService.getNextGame();
            if (optionalGame.isPresent()) {

                // If game countdown to start
                Game game = optionalGame.get();
                JSONArray leaders = logService.topPlayers();
                List<Integer> timeToStart = gameService.getTimeToStart(game);
                Integer hours = timeToStart.get(0);
                Integer minutes = timeToStart.get(1);
                Integer seconds = timeToStart.get(2);
                JSONObject time = new JSONObject();
                output.put("leaderboard", leaders);
                time.put("minutes", minutes.toString());
                time.put("seconds", seconds.toString());
                output.put("time", time);
                responseStatus = HttpStatus.PARTIAL_CONTENT;
            } else {

                // If no game
                responseStatus = HttpStatus.NO_CONTENT;
            }
        }



//        if (optionalGame.isPresent()) {
//
//
//
//
//            Game game = optionalGame.get();
//            List<Integer> timeToStart = gameService.getTimeToStart(game);
//            Integer hours = timeToStart.get(0);
//            Integer minutes = timeToStart.get(1);
//            Integer seconds = timeToStart.get(2);
//
//            if (hours <= 0 && minutes <= 0 && seconds <= 0) {
//
//            } else {
//                JSONObject time = new JSONObject();
//                time.put("minutes", minutes.toString());
//                time.put("seconds", seconds.toString());
//                output.put("time", time);
//                responseStatus = HttpStatus.PARTIAL_CONTENT;
//            }
//        } else {
//            responseStatus = HttpStatus.NO_CONTENT;
//        }



        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}
