package com.serendipity.gameController.control;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.json.JSONArray;

import java.util.*;

@Controller
public class MobileController {

  @RequestMapping(value="/update", method=RequestMethod.POST, consumes="application/json")
  public @ResponseBody
  String setTest(@RequestBody String json) {
      JSONObject obj = new JSONObject(json);

      Integer userId = obj.getInt("id");
      JSONArray array = obj.getJSONArray("beacons");

      for (int i = 0; i < array.length(); i++) {
          JSONObject b = array.getJSONObject(i);
          System.out.println(b.getInt("major") + " " + b.getFloat("rssi"));
      }
      //return new ResponseEntity(HttpStatus.OK);
      return "received";
  }

}