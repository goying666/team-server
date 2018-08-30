package com.renchaigao.zujuba.teamserver.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.dao.Player;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.UserOpenInfo;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.*;
import com.renchaigao.zujuba.mongoDB.info.team.teamGameInfo;
import com.renchaigao.zujuba.mongoDB.info.team.teamPlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.user.UserInfo;
import com.renchaigao.zujuba.teamserver.service.TeamService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import store.DistanceFunc;
import sun.misc.ObjectInputFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    public ResponseEntity createTeam(TeamInfo teamInfo) {
//        准备数据
        String userId = teamInfo.getCreaterId();
        UserInfo userInfo = mongoTemplate.findById(userId, UserInfo.class);
        UserOpenInfo userOpenInfo = userInfo.getUserOpenInfo();
        AddressInfo addressInfo = userInfo.getMyAddressInfo();

//        String userId = teamInfo.getCreaterId();
//        Criteria criteria = Criteria.where("teamInfo.createrId").is(userId);
//        List<TeamInfo> teamInfoList = mongoTemplate.find(Query.query(criteria),TeamInfo.class);
//        if (null != teamInfoList)
//            return new ResponseEntity(RespCode.TEAMHAD, teamInfo);
//        创建一个新的team
//        teaminfo信息完整
// 1、检查teamInfo信息是否完整，2、创建teamInfo相关的表，3，更新相关数据并回传最终结果；

//        set teamNumber

//        address part
        AddressInfo teamAddressInfo = teamInfo.getAddressInfo();
        mongoTemplate.save(addressInfo);
//        player part
        teamPlayerInfo teamPlayerInfo = teamInfo.getTeamPlayerInfo();
        PlayerInfo playerInfo = new PlayerInfo();//创建房主的player信息
        playerInfo.setHomeOwner("owner");
        playerInfo.setDistance(DistanceFunc.getDistance(addressInfo.getLatitude(), addressInfo.getLongitude(), teamAddressInfo.getLatitude(), teamAddressInfo.getLongitude()));
        playerInfo.setComeFrom("T");
        playerInfo.setState("WAITTING");
//        playersInfo;
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
        teamGameInfo teamGameInfo = teamInfo.getTeamGameInfo();
        mongoTemplate.save(teamGameInfo);
//        spend part
        teamSpendInfo teamSpendInfo = teamInfo.getTeamSpendInfo();
        mongoTemplate.save(teamSpendInfo);
//        message part
        teamMessageInfo teamMessageInfo = teamInfo.getTeamMessageInfo();
        mongoTemplate.save(teamMessageInfo);
//        filter part
        teamFilterInfo teamFilterInfo = teamInfo.getTeamFilterInfo();
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
    public ResponseEntity getTeamsByUserId(String userId, String getWay) {
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
        });        return new ResponseEntity(RespCode.SUCCESS, teamInfos);

    }

    @Override
    public ResponseEntity updateTeamInfo(Integer userId, Integer teamId, TeamInfo teamInfo) {
        return null;
    }

    @Override
    public ResponseEntity deleteTeam(TeamInfo teamInfo) {
        return null;
    }
}
