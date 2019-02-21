package com.renchaigao.zujuba.teamserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.UserOpenInfo;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.*;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import com.renchaigao.zujuba.teamserver.service.TeamJoinService;
import com.renchaigao.zujuba.teamserver.service.TeamService;
import com.renchaigao.zujuba.teamserver.uti.TeamDateFunc;
import normal.dateUse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import store.DistanceFunc;

import java.util.*;

@Service
public class TeamJoinServiceImpl implements TeamJoinService {

    private static Logger logger = Logger.getLogger(TeamJoinServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public ResponseEntity UserJoinTeam(String userInfoString,String teamInfoString) {
        Long funBegin = Calendar.getInstance().getTimeInMillis();
        String userId;
        String teamId;
        UserInfo _userInfo = JSONObject.parseObject(userInfoString,UserInfo.class);
        TeamInfo _teamInfo = JSONObject.parseObject(teamInfoString,TeamInfo.class);
        userId = _userInfo.getId();
        teamId = _teamInfo.getId();
//        获取team信息，
        TeamInfo teamInfo = mongoTemplate.findById(teamId, TeamInfo.class, "teamInfo");
//        获取team的筛选信息，判断加入的用户是否满足，若不满足则返回相应的提示。

//        增加用户信息
        UserOpenInfo userOpenInfo = mongoTemplate.findById(userId, UserOpenInfo.class, "userOpenInfo");
//        获取用户是否已存在，
        TeamPlayerInfo teamPlayerInfo = mongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                TeamPlayerInfo.class, "teamPlayerInfo");
        for (PlayerInfo s : teamPlayerInfo.getPlayerArrayList()) {
            if (s.getId().equals(userId))
                return new ResponseEntity(RespCode.TEAM_HAD_BEEN_JOIN, teamInfo);
        }
        PlayerInfo playerInfo = new PlayerInfo();
//        将user的信息填入playerInfo中
        playerInfo.setId(userId);
        playerInfo.setHomeOwner("0");
        playerInfo.setUserOpenInfo(userOpenInfo);
        playerInfo.setState("JOIN");
        playerInfo.setComeFrom("T");
        playerInfo.setJoinTime(dateUse.GetStringDateNow());
        mongoTemplate.save(playerInfo, "playerInfo");
//        用户距离————待开发；
//        修改teamPlayerInfo内的内容；
//        修改男女数量,并添加用户player信息；
        Update update = new Update();
//        如果有性别限制，则进行男女人数统计
//        if (userOpenInfo.getGender().equals("B")) {
//            update.inc("boySum", 1);
//        } else {
//            update.inc("girlSum", 1);
//        }
        update.inc("watingSum", 1).push("playerArrayList", playerInfo);
        mongoTemplate.updateFirst(Query.query(Criteria.where("teamId").is(teamId)), update,
                TeamPlayerInfo.class, "teamPlayerInfo");

//        更新用户的team信息
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(userId)),
                new Update().push("doingTeamsList", teamId).push("allTeamsList", teamId),
                UserTeamInfo.class, "myTeamsInfo");

        teamInfo.setTeamPlayerInfo(mongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                TeamPlayerInfo.class, "teamPlayerInfo"));

        teamInfo = mongoTemplate.findById(teamId, TeamInfo.class, "teamInfo");
        Long funEnd = Calendar.getInstance().getTimeInMillis();
        logger.info("This function spend times : " + (funEnd - funBegin) / 60000 + "m"
                + (funEnd - funBegin) / 1000 + "s" + (funEnd - funBegin) % 1000 + "ms");
        return new ResponseEntity(RespCode.SUCCESS, teamInfo);
    }

    //        获取team的筛选信息，判断加入的用户是否满足，若不满足则返回相应的提示。
    private ResponseEntity CheckUserInfo() {
        return null;
    }
}
