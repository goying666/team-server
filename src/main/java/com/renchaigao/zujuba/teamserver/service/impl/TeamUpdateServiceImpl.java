package com.renchaigao.zujuba.teamserver.service.impl;

import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.Player;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import com.renchaigao.zujuba.teamserver.service.TeamUpdateService;
import org.apache.log4j.Logger;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamUpdateServiceImpl implements TeamUpdateService {

    private static Logger logger = Logger.getLogger(TeamUpdateServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public ResponseEntity UpdateTeamInfo(String userId, String teamId) {
//        通过teamID获取team的info信息；
        TeamInfo teamInfo = mongoTemplate.findById(teamId,TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAMINFO);
//        判断获取team信息的user是否已经加入该team；
        TeamPlayerInfo playerInfo = mongoTemplate.findById(teamId,TeamPlayerInfo.class,MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_PLAYERINFO);
        List<PlayerInfo> playerArrayList = playerInfo.getPlayerArrayList();
        for (PlayerInfo p:playerArrayList){
            if(p.getId().equals(userId)){
//                说明用户已存在于该team中；
                return new ResponseEntity(RespCode.UPDATE_TEAM_SUCCESS_JOIN,teamInfo);
            }
        }
        return new ResponseEntity(RespCode.UPDATE_TEAM_SUCCESS,teamInfo);
    }
}
