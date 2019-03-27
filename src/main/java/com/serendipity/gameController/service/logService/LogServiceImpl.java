package com.serendipity.gameController.service.logService;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.repository.LogRepository;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.exposeService.ExposeServiceImpl;
import com.serendipity.gameController.service.interceptService.InterceptServiceImpl;
import com.serendipity.gameController.service.missionService.MissionServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.serendipity.gameController.service.zoneService.ZoneServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("logService")
public class LogServiceImpl implements LogService {

    @Autowired
    LogRepository logRepository;

    @Autowired
    MissionServiceImpl missionService;

    @Autowired
    ExposeServiceImpl exposeService;

    @Autowired
    ExchangeServiceImpl exchangeService;

    @Autowired
    InterceptServiceImpl interceptService;

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    ZoneServiceImpl zoneService;

    @Override
    public void saveLog(LogType type, Long id, LocalTime time, Zone zone){
        Log log = new Log(type, id, time, zone);
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
            if(!(l.getType() == LogType.EXCHANGE)){
                JSONObject obj = new JSONObject();
                String text = "";
                if (l.getType() == LogType.MISSION) {
                    // Compose string for mission log
                    // Get mission
                    Optional<Mission> opMission = missionService.getMission(l.getInteractionId());
                    // See if mission exists
                    if (opMission.isPresent()) {
                        Mission mission = opMission.get();
                        Optional<Player> opPlayer = playerService.getPlayerByMission(mission);
                        // See if player exists for the given mission assignment
                        if (opPlayer.isPresent()) {
                            Player player = opPlayer.get();
                            text = player.getRealName() + " completed a mission!";
                        }
                    }
                } else if (l.getType() == LogType.INTERCEPT) {
                    // Compose string for intercept log
                    // Get intercept
                    Optional<Intercept> opIntercept = interceptService.getIntercept(l.getInteractionId());
                    // See if intercept exists
                    if (opIntercept.isPresent()) {
                        Intercept intercept = opIntercept.get();
                        // Get player
                        Player player = intercept.getIntercepter();
                        // Get target
                        Exchange exchange = intercept.getExchange();
                        Player target1 = exchange.getRequestPlayer();
                        Player target2 = exchange.getResponsePlayer();
                        text = player.getRealName() + "<span class = \"intercept\"> intercepted</span> an <span class = \"exchange\">exchange</span> between " + target1.getRealName() + " and " + target2.getRealName() + "!";
                    }

                } else if (l.getType() == LogType.EXPOSE) {
                    // Compose string for expose log
                    // Get expose
                    Optional<Expose> opExpose = exposeService.getExpose(l.getInteractionId());
                    // See if expose exists
                    if (opExpose.isPresent()) {
                        Expose expose = opExpose.get();
                        Player player = expose.getPlayer();
                        Player target = expose.getTarget();
                        text = player.getRealName() + " <span class = \"expose\">exposed</span> " + target.getRealName() + "!";
                    }
                }
                obj.put("time", l.getTime());
                obj.put("message", text);
                output.put(obj);
                l.setSent(true);
                logRepository.save(l);
            }
        }
        return output;
    }

    @Override
    public JSONArray zoneDisplay() {
        JSONArray zones = new JSONArray();

        for(Zone z : zoneService.getAllZones()){
            int count = 0;
            int red = 0;
            int green = 0;
            // Select colour
            for(Log l : logRepository.findAllByZone(z)){
                // Add to correct colour
                switch(l.getType()) {
                    case EXCHANGE:
                        green += 255;
                        count++;
                        break;
                    case INTERCEPT:
                        red += 255;
                        green += 165;
                        count++;
                        break;
                    case EXPOSE:
                        red += 255;
                        count++;
                        break;
                    default:
                        break;
                }
            }
            if(red > 0) red /= count;
            if(green > 0) green /= count;
            // Construct rgb JSON
            JSONObject rgb = new JSONObject();
            rgb.put("red", red);
            rgb.put("green", green);
            rgb.put("blue", 0);
            // Get zone size
            List<Player> playersAtZone = playerService.getAllPlayersByCurrentZone(z);
            // Add to output
            JSONObject zoneInfo = new JSONObject();
            zoneInfo.put("zone_id", z.getId());
            zoneInfo.put("colour", rgb);
            zoneInfo.put("size", playersAtZone.size());
            zones.put(zoneInfo);
        }

        return zones;
    }

    @Override
    public JSONArray topPlayers(){
        JSONArray output = new JSONArray();
        List<Player> players = playerService.getAllPlayersByScore();
        int max = 3;
        if(max > players.size()) max = players.size();
        int count = 0;
        // Get number of players wanted
        while(count < max) {
            JSONObject player = new JSONObject();
            player.put("position", (count + 1));
            player.put("real_name", players.get(count).getRealName());
            player.put("reputation", players.get(count).getReputation());
            output.put(player);
            count++;
        }
        // TODO: what do we do if there's an overflow due to multiple people with the same position
        return output;
    }

}
