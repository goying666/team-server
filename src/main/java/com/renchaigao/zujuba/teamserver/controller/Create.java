package com.renchaigao.zujuba.teamserver.controller;


import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j
@Controller
@RequestMapping(value = "/create")
public class Create {

    @Autowired
    TeamServiceImpl teamServiceImpl;


    @PostMapping( consumes = "application/json")
    @ResponseBody
    public ResponseEntity createTeam(@RequestBody TeamInfo teamInfo) {
        return teamServiceImpl.createTeam(teamInfo);
    }

}
