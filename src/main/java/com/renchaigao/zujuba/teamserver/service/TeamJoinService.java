package com.renchaigao.zujuba.teamserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface TeamJoinService {

//    用户加入一个team
    ResponseEntity UserJoinTeam(String userInfoString,String teamInfoString);
}
