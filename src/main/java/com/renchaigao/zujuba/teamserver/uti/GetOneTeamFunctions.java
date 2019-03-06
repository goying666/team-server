package com.renchaigao.zujuba.teamserver.uti;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PageBean.CardPlayerInfoBean;
import com.renchaigao.zujuba.PageBean.TeamActivityBean;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.PropertiesConfig.PlayerConstant;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.domain.response.RespCode;
import com.renchaigao.zujuba.domain.response.ResponseEntity;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamGameInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import normal.dateUse;
import org.bouncycastle.jce.provider.symmetric.TEA;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import store.DistanceFunc;

import java.util.ArrayList;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static com.renchaigao.zujuba.PropertiesConfig.PhotoConstant.*;
import static com.renchaigao.zujuba.PropertiesConfig.PlayerConstant.*;

public class GetOneTeamFunctions {

    UserOpenInfoMapper userOpenInfoMapper;
    UserMapper userMapper;
    MongoTemplate mongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public GetOneTeamFunctions(UserMapper userMapper,
                               MongoTemplate mongoTemplate,
                               UserOpenInfoMapper userOpenInfoMapper,
                               KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
        this.userOpenInfoMapper = userOpenInfoMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：
     */
    public TeamInfo GetDatabaseInfo(String teamId) {
        return mongoTemplate.findById(teamId, TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
    }

    /*
     * 说明：组装teamActivity的页面参数
     */
    public TeamActivityBean AssembleOtherInfo(String userId, TeamInfo teamInfo) {
        TeamActivityBean teamActivityBean = new TeamActivityBean();
        //teamID
        teamActivityBean.setTeamId(teamInfo.getId());
        //teamName
        teamActivityBean.setTeamName(teamInfo.getTeamName());
        //浏览者类型
        if (teamInfo.getCreaterId().equals(userId)) {
            teamActivityBean.setUserClass("房主");
        } else {
            TeamPlayerInfo teamPlayerInfo = mongoTemplate.findById(teamInfo.getId(), TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
            for (PlayerInfo o : teamPlayerInfo.getPlayerArrayList()) {
                if (o.getId().equals(userId)) {
                    teamActivityBean.setUserClass("玩家");
                    break;
                } else {
                    teamActivityBean.setUserClass("游客");
                }
            }
        }
        //队伍状态
        switch (teamInfo.getState()) {
            case TEAM_STATE_WAITING:
                teamActivityBean.setTeamState("等待中");
                break;
            case TEAM_STATE_READY:
                teamActivityBean.setTeamState("准备完毕");
                break;
            case TEAM_STATE_ARRIVALS:
                teamActivityBean.setTeamState("全员到位");
                break;
            case TEAM_STATE_GAME:
                teamActivityBean.setTeamState("游戏中");
                break;
            case TEAM_STATE_FINISH:
                teamActivityBean.setTeamState("结束");
                break;
        }
//        地点图片url + 游戏图片
        ArrayList<String> imageUrlList = new ArrayList<>();
        TeamGameInfo teamGameInfo = teamInfo.getTeamGameInfo();
        if (teamGameInfo.isSelect_LRS())
            imageUrlList.add(LRS_GAME_IMAGE);
        if (teamGameInfo.isSelect_THQBY())
            imageUrlList.add(THQBY_GAME_IMAGE);
        if (teamGameInfo.isSelect_MXTSJ())
            imageUrlList.add(HASL_GAME_IMAGE);
        teamActivityBean.setTeamPhotoUrlList(imageUrlList);
//        地点名称
        teamActivityBean.setPlaceName(teamInfo.getPlaceName());
//        距离我的距离-//距离由页面传值，点击进去过一个
        AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
        Double userX = userAddress.getLatitude(), userY = userAddress.getLongitude();
        int distanc =
                DistanceFunc.getDistance(userX, userY,
                        teamInfo.getAddressInfo().getLatitude(),
                        teamInfo.getAddressInfo().getLongitude());
        if (distanc > 1000) {
            teamActivityBean.setDistance(Integer.toString(distanc / 1000) + "公里");
        } else {
            teamActivityBean.setDistance(distanc + "米");
        }
//        地点评分
        teamActivityBean.setPlaceScore("123");
//        地点名次
        teamActivityBean.setPlaceRank("321");
//        组局日期
        teamActivityBean.setStartDate(teamInfo.getStartDate());
//        组局开始时间
        if (teamInfo.getStartMinute() < 10) {
            teamActivityBean.setStartTime(
                    teamInfo.getStartHour() + ":0" + teamInfo.getStartMinute()
            );
        } else {
            teamActivityBean.setStartTime(
                    teamInfo.getStartHour() + ":" + teamInfo.getStartMinute()
            );
        }
//        结束时间
        if (teamInfo.getEndMinute() < 10) {
            teamActivityBean.setEndTime(
                    teamInfo.getEndHour() + ":0" + teamInfo.getEndMinute()
            );
        } else {
            teamActivityBean.setEndTime(
                    teamInfo.getEndHour() + ":" + teamInfo.getEndMinute()
            );
        }
//        组局倒计时
        JSONObject timeLave = dateUse.CompareStringDateAndTimeToNow(teamInfo.getStartDate(), teamInfo.getStartHour(), teamInfo.getStartMinute());
        if (timeLave.getIntValue("day") == 0) {
            if (timeLave.getIntValue("hour") == 0) {
                teamActivityBean.setTimeLeft(timeLave.getString("minute").replace("-", "") + "分后");
            } else
                teamActivityBean.setTimeLeft(timeLave.getString("hour").replace("-", "") + "小时后");
        } else {
            teamActivityBean.setTimeLeft(">24小时");
        }
//        已入局的玩家人数
        teamActivityBean.setAllPlayerNum(teamInfo.getPlayerNow().toString());
//        已入局的男玩家人数
        TeamPlayerInfo teamPlayerInfo = mongoTemplate.findById(teamInfo.getId(),
                TeamPlayerInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
        teamActivityBean.setBoyPlayerNum(teamPlayerInfo.getBoySum().toString());
//        已入局的女玩家人数
        teamActivityBean.setGirlPlayerNum(teamPlayerInfo.getGirlSum().toString());
//        组局最小人数
        teamActivityBean.setMinPlayer(teamInfo.getPlayerMin().toString());
//        组局最大人数
        teamActivityBean.setMaxPlayer(teamInfo.getPlayerMax().toString());
//        玩家card的bean信息列表
        ArrayList<CardPlayerInfoBean> cardPlayerInfoBeans = new ArrayList<>();
        for (PlayerInfo p : teamPlayerInfo.getPlayerArrayList()) {
            CardPlayerInfoBean cardPlayerInfoBean = new CardPlayerInfoBean();
//            头像url
            if (p.getUserOpenInfo().getPicPath() == null || p.getUserOpenInfo().getPicPath().equals("0")) {
                cardPlayerInfoBean.setImageUrl(USER_IMAGE_IMAGE);
            } else
                cardPlayerInfoBean.setImageUrl(p.getUserOpenInfo().getPicPath());
//            男女（默认无）
            cardPlayerInfoBean.setGender(p.getUserOpenInfo().getGender());
//            昵称
            cardPlayerInfoBean.setNickName(p.getUserOpenInfo().getNickName());
//                    组局次数
            if (p.getUserOpenInfo().getTeamTimes() == null) {
                cardPlayerInfoBean.setTeamTimes("0");
            } else
                cardPlayerInfoBean.setTeamTimes(p.getUserOpenInfo().getTeamTimes().toString());
//            游戏次数
            if (p.getUserOpenInfo().getGameTimes() == null) {
                cardPlayerInfoBean.setGameTimes("0");
            } else
                cardPlayerInfoBean.setGameTimes(p.getUserOpenInfo().getGameTimes().toString());
//                    年龄段
            if (p.getUserOpenInfo().getAgeLevel() == null) {
                cardPlayerInfoBean.setAgeFlag("新手");
            } else
                cardPlayerInfoBean.setAgeFlag(p.getUserOpenInfo().getAgeLevel());
//            状态
            switch (p.getState()) {
                case PLAYER_STATE_WAITING:
                    cardPlayerInfoBean.setTeamState("等待中");
                    break;
                case PLAYER_STATE_READY:
                    cardPlayerInfoBean.setTeamState("已准备");
                    break;
                case PLAYER_STATE_ARRIVALS:
                    cardPlayerInfoBean.setTeamState("已到场");
                    break;
                case PLAYER_STATE_GAME:
                    cardPlayerInfoBean.setTeamState("游戏中");
                    break;
                case PLAYER_STATE_QUIT:
                    cardPlayerInfoBean.setTeamState("离场");
                    break;
                case PLAYER_STATE_FINISH:
                    cardPlayerInfoBean.setTeamState("结束");
                    break;
            }
//                    简介
            if (p.getUserOpenInfo().getOpenNote() == null) {
                cardPlayerInfoBean.setUserNote("0");
            } else
                cardPlayerInfoBean.setUserNote(p.getUserOpenInfo().getOpenNote());
            cardPlayerInfoBeans.add(cardPlayerInfoBean);
        }
        teamActivityBean.setPlayerList(cardPlayerInfoBeans);
        return teamActivityBean;
    }
}
