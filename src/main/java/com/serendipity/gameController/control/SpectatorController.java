package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.logService.LogService;
import com.serendipity.gameController.service.logService.LogServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
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

    @Autowired
    PlayerServiceImpl playerService;

    @GetMapping(value="/spectator")
    public ResponseEntity<String> spectator() {
        JSONObject output = new JSONObject();
        HttpStatus responseStatus = HttpStatus.OK;

        // LEADERBOARD
        JSONArray leaders = logService.topPlayers();
        output.put("leaderboard", leaders);

        Optional<Game> optionalCurrentGame = gameService.getCurrentGame();
        if (optionalCurrentGame.isPresent()) {
            // There is a game being played
            Game currentGame = optionalCurrentGame.get();

            // TIME
            List<Integer> timeRemaining = gameService.getTimeRemaining(currentGame);
            Integer minutes = timeRemaining.get(1);
            Integer seconds = timeRemaining.get(2);
            String minutesString = minutes.toString();
            if (minutes < 10) minutesString = "0".concat(minutes.toString());
            String secondsString = seconds.toString();
            if (seconds < 10) secondsString = "0".concat(seconds.toString());
            String time = minutesString.concat(":").concat(secondsString);
            output.put("time", time);
            output.put("countdown_message", "TIME REMAINING");

            // LOGS
            JSONArray logs = logService.logOutput();
            output.put("logs", logs);
            output.put("clear_logs", false);

            // ZONES
            JSONArray zones = logService.zoneDisplay();
            output.put("zones", zones);
        } else {
            Optional<Game> optionalNextGame = gameService.getNextGame();

            // LOGS
            JSONArray logs = new JSONArray();
            output.put("logs", logs);

            if (optionalNextGame.isPresent()) {
                // There is a game queued
                Game nextGame = optionalNextGame.get();

                // ZONES
                JSONArray zones = logService.zoneDisplayBlank();
                output.put("zones", zones);

                if (playerService.countAllPlayers() >= nextGame.getMinPlayers()) {
                    // Game starting

                    // TIME
                    List<Integer> timeToStart = gameService.getTimeToStart(nextGame);
                    Integer minutes = timeToStart.get(1);
                    Integer seconds = timeToStart.get(2);
                    String minutesString = minutes.toString();
                    if (minutes < 10) minutesString = "0".concat(minutes.toString());
                    String secondsString = seconds.toString();
                    if (seconds < 10) secondsString = "0".concat(seconds.toString());
                    String time = minutesString.concat(":").concat(secondsString);
                    output.put("time", time);
                    output.put("countdown_message", "GAME STARTING");

                    // CLEAR LOGS
                    output.put("clear_logs", false);
                } else {
                    // Waiting for players

                    // TIME
                    String time = "--:--";
                    output.put("time", time);
                    output.put("countdown_message", "WAITING FOR PLAYERS");

                    // CLEAR LOGS
                    output.put("clear_logs", true);
                }
            } else {
                Optional<Game> optionalPreviousGame = gameService.getPreviousGame();

                // ZONES
                JSONArray zones = logService.zoneDisplayBlank();
                output.put("zones", zones);

                // CLEAR LOGS
                output.put("clear_logs", false);

                if (optionalPreviousGame.isPresent()) {
                    // A game ended and a new one hasn't been queued yet

                    // TIME
                    String time = "00:00";
                    output.put("time", time);
                    output.put("countdown_message", "GAME OVER");
                } else {
                    // There are no games in the database

                    // TIME
                    String time = "--:--";
                    output.put("time", time);
                    output.put("countdown_message", "NO GAME AVAILABLE");
                }
            }
        }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}
