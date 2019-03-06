package com.renchaigao.zujuba.teamserver.service;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface TeamService {
    //    用户创建一个team
    ResponseEntity CreateNewTeam(String userId, String teamId, String jsonObjectString);

    //    用户获取以自己为信息源的teams信息（根据相关参数从数据库中拿teams的信息，例如：距离、筛选项）
    ResponseEntity GetNearTeams(String userId, String parameter, String jsonObjectString);

    ResponseEntity FindOneTeam(String userId, String teamId, String jsonObjectString);

    ResponseEntity JoinTeam(String userId, String teamId, String jsonObjectString);

    //    //    team相关信息的更新（基本设置、游戏配置、人员变动等）
    ResponseEntity UpdateTeam(String userId, String teamId, String parameter, String jsonObjectString);
//
//
//    ResponseEntity QuitTeam(String userId, String parameter, String teamId, String jsonObjectString);
//
//    //    team删除，用户取消，或者历史数据删除。
//    ResponseEntity DeleteTeam(String userId, String parameter, String teamId, String jsonObjectString);
//
//    ResponseEntity ReportTeam(String userId, String parameter, String teamId, String jsonObjectString);
//
//    //    通过用户id，获取其个人的team信息，包含创建的组局和加入过的组局；
//    ResponseEntity FindMyTeams(String userId, String parameter, String teamId, String jsonObjectString);
//

//
//    ResponseEntity DeleteMyTeams(String userId, String parameter, String teamId, String jsonObjectString);


}
