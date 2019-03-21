package com.serendipity.gameController.service.logService;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.repository.LogRepository;
import com.serendipity.gameController.service.exchangeService.ExchangeService;
import com.serendipity.gameController.service.exposeService.ExposeService;
import com.serendipity.gameController.service.interceptService.InterceptService;
import com.serendipity.gameController.service.missionService.MissionService;
import com.serendipity.gameController.service.playerService.PlayerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service("logService")
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    MissionService missionService;

    @Autowired
    ExposeService exposeService;

    @Autowired
    ExchangeService exchangeService;

    @Autowired
    InterceptService interceptService;

    @Autowired
    PlayerService playerService;

    @Override
    public void saveLog(LogType type, Long id, LocalTime time){
        Log log = new Log(type, id, time);
        logRepository.save(log);
    }

    @Override
    public Optional<Log> getLog(Long id){
        return logRepository.findById(id);
    }

    @Override
    public void deleteAllLogs() {
        logRepository.deleteAll();
    }

    @Override
    public JSONArray logOutput() {
        JSONArray output = new JSONArray();

        // Get all non-sent logs
        List<Log> logs = logRepository.findAllBySent(false);

        for (Log l : logs) {
            JSONObject obj = new JSONObject();
            String text = "";
            if(l.getType() == LogType.MISSION){
                // Compose string for mission log
                // Get mission
                Optional<Mission> opMission = missionService.getMission(l.getInteractionId());
                // See if mission exists
                if(opMission.isPresent()){
                    Mission mission = opMission.get();
                    Optional<Player> opPlayer = playerService.getPlayerByMission(mission);
                    // See if player exists for the given mission assignment
                    if(opPlayer.isPresent()){
                        Player player = opPlayer.get();
                        text = player.getRealName() + " completed a mission!";
                    }
                }
            } else if (l.getType() == LogType.INTERCEPT){
                // Compose string for intercept log
                // Get intercept
                Optional<Intercept> opIntercept = interceptService.getIntercept(l.getInteractionId());
                // See if intercept exists
                if(opIntercept.isPresent()) {
                    Intercept intercept = opIntercept.get();
                    // Get player
                    Player player = intercept.getIntercepter();
                    // Get target
                    Exchange exchange = intercept.getExchange();
                    Player target1 = exchange.getRequestPlayer();
                    Player target2 = exchange.getResponsePlayer();
                    text = player.getRealName() + "<span class = \"intecept\">intercepted<\\span> an <span class = \"exchange\">exchange<\\span> between " + target1.getRealName() + " and " + target2.getRealName() + "!";
                }

            } else if (l.getType() == LogType.EXPOSE){
                // Compose string for expose log
                // Get expose
                Optional<Expose> opExpose = exposeService.getExpose(l.getInteractionId());
                // See if expose exists
                if(opExpose.isPresent()){
                    Expose expose = opExpose.get();
                    Player player = expose.getPlayer();
                    Player target = expose.getTarget();
                    text = player.getRealName() + " <span class = \"expose\">exposed<\\span> " + target.getRealName() + "!";
                }
            }
            obj.put(l.getTime().toString(), text);
            output.put(obj);
        }

        System.out.println(output);

        return output;
    }

}
