package com.renchaigao.zujuba.teamserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import com.renchaigao.zujuba.teamserver.service.impl.TeamServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Log4j
@Controller
@RequestMapping()
public class TeamController {
    @Autowired
    TeamServiceImpl teamServiceImpl;

    @Autowired
    UserMapper userMapper;


    @PostMapping(value = "/{firstStr}/{secondStr}/{userId}/{userToken}/{teamId}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity TeamControllerFuns(@PathVariable("firstStr") String fistStr,
                                             @PathVariable("secondStr") String secondStr,
                                             @PathVariable("userId") String userId,
                                             @PathVariable("userToken") String userToken,
                                             @PathVariable("teamId") String teamId,
                                             @RequestBody String jsonObjectString) {
        if (!userMapper.selectByPrimaryKey(userId).getToken().equals(userToken))
            return new ResponseEntity(RespCode.AUTHENTICATIONFAIL, null);
        switch (fistStr) {
            case "create":
                return teamServiceImpl.CreateNewTeam(userId, secondStr, teamId, jsonObjectString);
            case "update":
                return teamServiceImpl.UpdateTeam(userId, secondStr, teamId, jsonObjectString);
            case "join":
                return teamServiceImpl.JoinTeam(userId, secondStr, teamId, jsonObjectString);
            case "quit":
                return teamServiceImpl.QuitTeam(userId, secondStr, teamId, jsonObjectString);
            case "delete":
                return teamServiceImpl.DeleteTeam(userId, secondStr, teamId, jsonObjectString);
            case "report":
                return teamServiceImpl.ReportTeam(userId, secondStr, teamId, jsonObjectString);
            case "getnear":
                return teamServiceImpl.GetNearTeams(userId, secondStr, teamId, jsonObjectString);
            case "getcon":
                return teamServiceImpl.GetNearTeams(userId, secondStr, teamId, jsonObjectString);
            case "deletemine":
                return teamServiceImpl.DeleteMyTeams(userId, secondStr, teamId, jsonObjectString);
        }
        return new ResponseEntity(RespCode.WRONGIP,null);
    }

//    private Boolean UserAuthenticationFun(String userId,String userToken){
//        User user = userMapper.selectByPrimaryKey(userId);
//        if(user.getToken().equals(userToken))
//            return true;
//        else return false;
//    }

}
