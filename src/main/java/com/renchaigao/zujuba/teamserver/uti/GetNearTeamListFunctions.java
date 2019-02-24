package com.renchaigao.zujuba.teamserver.uti;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.renchaigao.zujuba.PropertiesConfig.MongoDBCollectionsName;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.dao.mapper.UserOpenInfoMapper;
import com.renchaigao.zujuba.mongoDB.info.AddressInfo;
import com.renchaigao.zujuba.mongoDB.info.PlayerInfo;
import com.renchaigao.zujuba.mongoDB.info.store.StoreInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import normal.dateUse;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import store.DistanceFunc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.renchaigao.zujuba.PropertiesConfig.ConstantManagement.*;

public class GetNearTeamListFunctions {

    UserOpenInfoMapper userOpenInfoMapper;
    UserMapper userMapper;
    MongoTemplate mongoTemplate;

    public GetNearTeamListFunctions(UserMapper userMapper, MongoTemplate mongoTemplate, UserOpenInfoMapper userOpenInfoMapper) {
        this.userMapper = userMapper;
        this.mongoTemplate = mongoTemplate;
        this.userOpenInfoMapper = userOpenInfoMapper;
    }

    /*
     * 说明：获取原始信息 teamInfoList
     */
    public ArrayList<TeamInfo> GetNearTeamInfoListByUserId(String userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        AddressInfo userAddress = mongoTemplate.findById(userId, AddressInfo.class);
        String userCityCode = userAddress.getCitycode();
        Double userX = userAddress.getLatitude(), userY = userAddress.getLongitude();
        List<TeamInfo> teamInfos = mongoTemplate.find(
                Query.query(Criteria.where("addressInfo.citycode").is(userCityCode)), TeamInfo.class, MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_INFO);
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
        return new ArrayList<>(teamInfos);
    }


    /*
     * 说明：组装参数给前端
     */
    public JSONArray PackageTeamInfoList(ArrayList<TeamInfo> teamInfoArrayList) {
        JSONArray jsonArray = new JSONArray();
        for (TeamInfo o : teamInfoArrayList) {
            JSONObject json = new JSONObject();
            json.put("teamId", o.getId());
            json.put("name", o.getTeamName());
            json.put("state", o.getState());
            switch (o.getAddressInfo().getAddressClass()) {
                case ADDRESS_CLASS_USER:
                    break;
                case ADDRESS_CLASS_STORE:
                    StoreInfo storeInfo = mongoTemplate.findById(
                            o.getAddressInfo().getId(), StoreInfo.class,
                            MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_STORE_INFO);
                    json.put("place", storeInfo.getName());
//                    __后期需要修改成 店铺的特殊标识，现在暂用“5分”替代
                    json.put("placeNote", "5分");
                    json.put("ownerid", storeInfo.getOwnerId());
                    json.put("imageurl", "showimage/" + storeInfo.getOwnerId() + "/" + storeInfo.getId() + "/photo1.jpg");
                    json.put("placeid", storeInfo.getId());
                    break;
                case ADDRESS_CLASS_OPEN:
                    break;
                case ADDRESS_CLASS_HOME:
                    break;
                case ADDRESS_CLASS_SCHOOL:
                    break;
            }
            json.put("rating", 5);//-待完善
            json.put("distance", o.getAddressInfo().getDistance());
            TeamPlayerInfo teamPlayerInfo = mongoTemplate.findById(o.getId(), TeamPlayerInfo.class,MongoDBCollectionsName.MONGO_DB_COLLECIONS_NAME_TEAM_PLAYER_INFO);
            json.put("boynum", teamPlayerInfo.getBoySum());
            json.put("girlnum", teamPlayerInfo.getGirlSum());
            json.put("currentPlayer", teamPlayerInfo.getPlayerArrayList().size());
            int day = dateUse.CompareTwoStringDate(dateUse.getTodayDate(), o.getStartDate());
            if (day == 0) {
                json.put("date", "今天");
            } else {
                json.put("date", day + "天后");
            }
            if (o.getStartMinute() < 10)
                json.put("time", o.getStartHour() + ":0" + o.getStartMinute());
            else
                json.put("time", o.getStartHour() + ":" + o.getStartMinute());
            JSONObject timeLave = dateUse.CompareStringDateAndTimeToNow(o.getStartDate(), o.getStartHour(), o.getStartMinute());
            if (timeLave.getIntValue("day") == 0) {
                json.put("lave", timeLave.getIntValue("hour" + "h" + timeLave.getIntValue("minute" + "m")));
            } else {
                json.put("lave", "24h+");
            }
            jsonArray.add(json);
        }
        return jsonArray;
    }

}
