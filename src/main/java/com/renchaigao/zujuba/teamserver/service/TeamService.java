package com.renchaigao.zujuba.teamserver.service;

import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public interface TeamService {
//    用户创建一个team
    ResponseEntity createTeam(TeamInfo teamInfo);
//    用户获取以自己为信息源的teams信息（根据相关参数从数据库中拿teams的信息，例如：距离、筛选项）
    ResponseEntity getTeamsByUserId(Integer userId, String getWay);
//    team相关信息的更新（基本设置、游戏配置、人员变动等）
    ResponseEntity updateTeamInfo(Integer userId, Integer teamId, TeamInfo teamInfo);
//    team删除，用户取消，或者历史数据删除。
    ResponseEntity deleteTeam(TeamInfo teamInfo);
}
