package com.renchaigao.zujuba.teamserver.uti;

import com.renchaigao.zujuba.dao.Address;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class CreateNewTeamFunctions {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserOpenInfoMapper userOpenInfoMapper;

    @Autowired
    UserMapper userMapper;

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
    }

    /*
     * 说明：地址信息
     */
    public void CreateTeamInfoAddress(TeamInfo teamInfo) {
        Address createAddress = teamInfo.getAddressInfo();
        switch (createAddress.getAddressClass()) {
            case "store":
//                通知store-server有新组局创建；
//                更新场地组局信息（增加）
                break;
            case "open":
//                自检内容：1、人数超标是否？
//                更新场地组局信息（增加）
                break;
//                未开放功能；
            case "user":
                break;
        }
    }

    /*
     * 说明：玩家信息
     */
    public void CreateTeamInfoPlayer(TeamInfo teamInfo) {
    }

    /*
     * 说明：游戏信息
     */
    public void CreateTeamInfoGame(TeamInfo teamInfo) {
    }

    /*
     * 说明：筛选条件信息
     */
    public void CreateTeamInfoFilter(TeamInfo teamInfo) {
    }

    /*
     * 说明：消费信息
     */
    public void CreateTeamInfoSpend(TeamInfo teamInfo) {
    }

    /*
     * 说明：组队消息信息
     */
    public void CreateTeamInfoMessage(TeamInfo teamInfo) {
    }

    /*
     * 说明：请求所有信息
     */
    public void CreateTeamInfoAll(TeamInfo teamInfo) {
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
    }

}
