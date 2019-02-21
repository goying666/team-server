package com.renchaigao.zujuba.teamserver.controller;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.teamserver.service.impl.TeamJoinServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j
@Controller
@RequestMapping(value = "/join")
public class TeamJoinController {

    @Autowired
    TeamJoinServiceImpl teamJoinServiceImpl;

    @PostMapping(value = "/join/{userId}/{teamId}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity UserJoinTeam(@PathVariable("userId") String userId,
                                       @PathVariable("teamId") String teamId,
                                       @RequestParam("userInfo") String userInfoString,
                                       @RequestParam("teamInfo") String teamInfoString) {
        return teamJoinServiceImpl.UserJoinTeam(userInfoString, teamInfoString);
    }
}
