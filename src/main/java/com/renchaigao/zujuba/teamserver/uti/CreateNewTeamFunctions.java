package com.renchaigao.zujuba.teamserver.uti;

import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.Address;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;
import static com.renchaigao.zujuba.PropertiesConfig.GameConstant.GAME_AHSL;
import static com.renchaigao.zujuba.PropertiesConfig.GameConstant.GAME_LRS;
import static com.renchaigao.zujuba.PropertiesConfig.GameConstant.GAME_THQBY;
import static com.renchaigao.zujuba.PropertiesConfig.KafkaTopicConstant.*;

public class CreateNewTeamFunctions {

    UserOpenInfoMapper userOpenInfoMapper;
    UserMapper userMapper;
    MongoTemplate mongoTemplate;
    KafkaTemplate<String, String> kafkaTemplate;

    public CreateNewTeamFunctions(UserMapper userMapper, MongoTemplate mongoTemplate, UserOpenInfoMapper userOpenInfoMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
        this.userOpenInfoMapper = userOpenInfoMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /*
     * 说明：检查用户创建组局的数据是否无误
     * 检查项：1、缺失类；2、违规类；
     */
    public Boolean CheckCreateInfo(String teamInfoString) {
        return true;
    }

    /*
     * 说明：基础信息,用户创建时填入的部分
     */
    public void CreateTeamInfoBasic(TeamInfo teamInfo) {
        mongoTemplate.save(teamInfo);
//        组队名创建：游戏+人数+“局”
        String teamName = "";
        if (teamInfo.getTeamGameInfo().isSelect_LRS()) {
            teamName = GAME_LRS;
            if (teamInfo.getTeamGameInfo().isSelect_THQBY()) {
                teamName += "," + GAME_THQBY;
            }
            if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                teamName += "," + GAME_AHSL;
            }
        } else {
            if (teamInfo.getTeamGameInfo().isSelect_THQBY()) {
                teamName = GAME_THQBY;
                if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                    teamName += "," + GAME_AHSL;
                }
            }else {
                if (teamInfo.getTeamGameInfo().isSelect_MXTSJ()) {
                    teamName = "," + GAME_AHSL;
                }
            }
        }
        teamName += teamInfo.getPlayerMin().toString() + "~" +teamInfo.getPlayerMax().toString() +"人局";
        teamInfo.setTeamName(teamName);

        teamInfo.setStartAllTime(TeamDateFunc.teamTimeFunc(teamInfo.getStartDate(), teamInfo.getStartTime()));

//        设置team编号：按照同城市0~999999来

    }

    /*
     * 说明：地址信息
     */
    public void CreateTeamInfoAddress(TeamInfo teamInfo) {
        Address createAddress = mongoTemplate.findById(teamInfo.getAddressInfo().getId(), AddressInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_ADDRESS_INFO);
        switch (createAddress.getAddressClass()) {
            case ADDRESS_CLASS_STORE:
//                通知place-server有新组局创建；
                kafkaTemplate.send(CREATE_NEW_TEAM, JSONObject.toJSONString(teamInfo));
//                更新场地组局信息（增加）

                break;
            case ADDRESS_CLASS_OPEN:
//                自检内容：1、人数超标是否？
//                更新场地组局信息（增加）
                break;
//                未开放功能；
            case ADDRESS_CLASS_USER:
                break;
        }
    }

    /*
     * 说明：玩家信息
     */
    public void CreateTeamInfoPlayer(TeamInfo teamInfo) {
//        player-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
//        修改team内有关player的数据；
        teamInfo.setPlayerNow(1);
    }

    /*
     * 说明：游戏信息
     */
    public void CreateTeamInfoGame(TeamInfo teamInfo) {
//        game-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
//        待开发
    }

    /*
     * 说明：筛选条件信息
     */
    public void CreateTeamInfoFilter(TeamInfo teamInfo) {
//        待开发

    }

    /*
     * 说明：消费信息
     */
    public void CreateTeamInfoSpend(TeamInfo teamInfo) {
//        待开发

    }

    /*
     * 说明：组队消息信息
     */
    public void CreateTeamInfoMessage(TeamInfo teamInfo) {
//        message-server 也订阅了 topic为：CREATE_NEW_TEAM 的消息，所以不再进行通知；
    }

    /*
     * 说明：请求所有信息
     */
    public void CreateTeamInfoAll(TeamInfo teamInfo) {
//        待开发
    }

    /*
     * 说明：检查组局信息是否完整
     */
    public void CheckAllInfoIsRight(TeamInfo teamInfo) {
    }

    /*
     * 说明：更新teamInfo信息
     */
    public void UpdateTeamInfoAll(TeamInfo teamInfo) {

    }

    /*
     * 说明：修改个人team信息
     */
    public void UpdateMyTeamsInfo(TeamInfo teamInfo) {
//        userTeam
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(teamInfo.getCreaterId())),
                new Update().push("userTeams.allTeamsList",teamInfo.getId())
                        .push("userTeams.doingTeamsList",teamInfo.getId()),
                MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_USER_TEAMS);
    }

}
