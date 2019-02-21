package com.renchaigao.zujuba.teamserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface TeamUpdateService {

    ResponseEntity UpdateTeamInfo(String userId, String teamId);
}
