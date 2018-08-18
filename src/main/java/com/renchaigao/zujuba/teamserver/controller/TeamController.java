package com.renchaigao.zujuba.teamserver.controller;

import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Log4j
@Controller
@RequestMapping(value = "/team")
public class TeamController {
    @Autowired
    TeamServiceImpl teamServiceImpl;


    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    public ResponseEntity createTeam(@RequestBody TeamInfo teamInfo) {
        log.info("create");
        return teamServiceImpl.createTeam(teamInfo);
    }

    @GetMapping(value = "/get/{userid}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity getTeam(@PathVariable("userid") Integer userid) {
        return teamServiceImpl.getTeamsByUserId(userid,null);
    }

}
