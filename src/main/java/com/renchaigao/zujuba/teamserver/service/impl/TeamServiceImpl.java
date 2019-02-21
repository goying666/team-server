package com.renchaigao.zujuba.teamserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
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
import com.renchaigao.zujuba.teamserver.service.TeamService;
import com.renchaigao.zujuba.teamserver.uti.CreateNewTeamFunctions;
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
public class TeamServiceImpl implements TeamService {

    private static Logger logger = Logger.getLogger(TeamServiceImpl.class);

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public ResponseEntity CreateNewTeam(String userId, String parameter, String teamId, String jsonObjectString) {

        CreateNewTeamFunctions createNewTeamFunctions = new CreateNewTeamFunctions();
        TeamInfo teamInfo = JSONObject.parseObject(jsonObjectString, TeamInfo.class);
        UserInfo userInfo = mongoTemplate.findById(userId, UserInfo.class);
        UserOpenInfo userOpenInfo = userInfo.getUserOpenInfo();

//        检查创建信息完整性 和 正确性（查重、冲突逻辑等）；
        if (!createNewTeamFunctions.CheckCreateInfo(JSONObject.toJSONString(teamInfo)))
            return null;
//        基础信息
        createNewTeamFunctions.CreateTeamInfoBasic(teamInfo);
//        地址信息
        createNewTeamFunctions.CreateTeamInfoAddress(teamInfo);
//        玩家信息
        createNewTeamFunctions.CreateTeamInfoPlayer(teamInfo);
//        游戏信息
        createNewTeamFunctions.CreateTeamInfoGame(teamInfo);
//        筛选条件信息
        createNewTeamFunctions.CreateTeamInfoFilter(teamInfo);
//        消费信息
        createNewTeamFunctions.CreateTeamInfoSpend(teamInfo);
//        组队消息信息
        createNewTeamFunctions.CreateTeamInfoMessage(teamInfo);
//        请求所有信息
        createNewTeamFunctions.CreateTeamInfoAll(teamInfo);
//        检查组局信息是否完整
        createNewTeamFunctions.CheckAllInfoIsRight(teamInfo);
//        更新teamInfo信息
        createNewTeamFunctions.UpdateTeamInfoAll(teamInfo);
//        修改个人team信息，myTeamsInfo
        createNewTeamFunctions.UpdateMyTeamsInfo(teamInfo);
//        返回创建结果


        AddressInfo addressInfo = userInfo.getAddressInfo();
        addressInfo.setId(teamInfo.getId());

//        String userId = teamInfo.getCreaterId();
//        Criteria criteria = Criteria.where("teamInfo.createrId").is(userId);
//        List<TeamInfo> teamInfoList = mongoTemplate.find(Query.query(criteria),TeamInfo.class);
//        if (null != teamInfoList)
//            return new ResponseEntity(RespCode.TEAMHAD, teamInfo);
//        创建一个新的team
//        teaminfo信息完整
//        1、检查teamInfo信息是否完整，2、创建teamInfo相关的表，3，更新相关数据并回传最终结果；

//        set teamNumber

//        Time Part
        teamInfo.setStartAllTime(TeamDateFunc.teamTimeFunc(teamInfo.getStartDate(), teamInfo.getStartTime()));

//        address part
        AddressInfo teamAddressInfo = teamInfo.getAddressInfo();
        mongoTemplate.save(addressInfo);
//        player part
        TeamPlayerInfo teamPlayerInfo = teamInfo.getTeamPlayerInfo();
        PlayerInfo playerInfo = new PlayerInfo();//创建房主的player信息
        playerInfo.setId(userInfo.getId());
        playerInfo.setHomeOwner("owner");
        playerInfo.setDistance(DistanceFunc.getDistance(addressInfo.getLatitude(), addressInfo.getLongitude(), teamAddressInfo.getLatitude(), teamAddressInfo.getLongitude()));
        playerInfo.setComeFrom("T");
        playerInfo.setState("WAITTING");
        playerInfo.setUserOpenInfo(userOpenInfo);
//        playersInfo;
        teamPlayerInfo.setId(teamInfo.getId());
        teamPlayerInfo.setPlayerArrayList(new ArrayList<>());
        teamPlayerInfo.getPlayerArrayList().add(playerInfo);
        teamPlayerInfo.setTeamId(teamInfo.getId());
        if (userOpenInfo.getGender() != "girl")
            teamPlayerInfo.setBoySum(1);
        else
            teamPlayerInfo.setGirlSum(1);
        teamPlayerInfo.setWatingSum(1);
        teamPlayerInfo.setMissSum(teamInfo.getPlayerMin() - 1);
        mongoTemplate.save(teamPlayerInfo);
//        game part
        TeamGameInfo teamGameInfo = teamInfo.getTeamGameInfo();
        teamGameInfo.setId(teamInfo.getId());
        mongoTemplate.save(teamGameInfo);
//        spend part
        TeamSpendInfo teamSpendInfo = teamInfo.getTeamSpendInfo();
        teamSpendInfo.setId(teamInfo.getId());
        mongoTemplate.save(teamSpendInfo);
//        message part
        TeamMessageInfo teamMessageInfo = teamInfo.getTeamMessageInfo();
        teamMessageInfo.setId(teamInfo.getId());
        mongoTemplate.save(teamMessageInfo);
//        filter part
        TeamFilterInfo teamFilterInfo = teamInfo.getTeamFilterInfo();
        teamFilterInfo.setId(teamInfo.getId());
        mongoTemplate.save(teamFilterInfo);

        mongoTemplate.insert(teamInfo);
////        2、数据库新增team信息；
//        teamInfo.setAddressId(addressInfo.getId());
//        teamInfo.setFilterId(filterInfo.getId());
//        teamInfo.setPlayerinfoId(playerInfo.getId());
//        teamInfo.setOwnerId(userId);
//        teamInfo.setTeamstate("waiting");
//        teamMapper.insert(teamInfo);
//        3、更新user相关team的信息；
//        4、更新redis中team的信息；
//        5、更新team对应的address的信息；
//        6、更新user在redis中的

        return new ResponseEntity(RespCode.SUCCESS, JSONObject.toJSONString(teamInfo));

//        return null;
    }

    @Override
    public ResponseEntity UpdateTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity JoinTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        Long funBegin = Calendar.getInstance().getTimeInMillis();
//        获取team信息，
        TeamInfo teamInfo = mongoTemplate.findById(teamId, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAMINFO);
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
                UserTeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);

        teamInfo.setTeamPlayerInfo(mongoTemplate.findOne(Query.query(Criteria.where("teamId").is(teamId)),
                TeamPlayerInfo.class, "teamPlayerInfo"));

        teamInfo = mongoTemplate.findById(teamId, TeamInfo.class, "teamInfo");
        Long funEnd = Calendar.getInstance().getTimeInMillis();
        logger.info("This function spend times : " + (funEnd - funBegin) / 60000 + "m"
                + (funEnd - funBegin) / 1000 + "s" + (funEnd - funBegin) % 1000 + "ms");
        return new ResponseEntity(RespCode.SUCCESS, teamInfo);
    }

    @Override
    public ResponseEntity QuitTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity DeleteTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity ReportTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity FindMyTeams(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity GetNearTeams(String userId, String parameter, String teamId, String jsonObjectString) {

        User user = userMapper.selectByPrimaryKey(userId);
        AddressInfo userAddress = mongoTemplate.findById(user.getMyAddressId(), AddressInfo.class);
        String userCityCode = userAddress.getCitycode();
        Double userX = userAddress.getLatitude(), userY = userAddress.getLongitude();
        List<TeamInfo> teamInfos = mongoTemplate.find(
                Query.query(Criteria.where("addressInfo.citycode").is(userCityCode)), TeamInfo.class);
        for (int i = 0; i < teamInfos.size(); i++) {
            teamInfos.get(i).getAddressInfo().setDistance(DistanceFunc.getDistance(userX, userY,
                    teamInfos.get(i).getAddressInfo().getLatitude(),
                    teamInfos.get(i).getAddressInfo().getLongitude())
            );
        }
        Collections.sort(teamInfos, new Comparator<TeamInfo>() {
            @Override
            public int compare(TeamInfo o1, TeamInfo o2) {
//                    从小到大
                return (int) (o1.getAddressInfo().getDistance() - o2.getAddressInfo().getDistance());
//                    从大到小
//                return (int)(o2.getDistance() - o1.getDistance());
            }
        });
        return new ResponseEntity(RespCode.SUCCESS, teamInfos);

    }

    @Override
    public ResponseEntity FindOneTeam(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }

    @Override
    public ResponseEntity DeleteMyTeams(String userId, String parameter, String teamId, String jsonObjectString) {
        return null;
    }
}
