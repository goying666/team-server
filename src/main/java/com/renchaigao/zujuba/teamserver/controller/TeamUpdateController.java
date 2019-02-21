package com.renchaigao.zujuba.teamserver.controller;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import com.renchaigao.zujuba.teamserver.service.impl.TeamUpdateServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Log4j
@Controller
@RequestMapping(value = "/update")
public class TeamUpdateController {
    @Autowired
    TeamUpdateServiceImpl teamServiceImpl;

    @GetMapping(value = "/{userid}/{teamid}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity updateTeamInfo(@PathVariable("userid") String userid,
                                         @PathVariable("teamid") String teamid) {
        return teamServiceImpl.UpdateTeamInfo(userid, teamid);
    }

}
