package com.renchaigao.zujuba.teamserver;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeamServerApplicationTests {

    private static Logger logger = Logger.getLogger(TeamServerApplicationTests.class);
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserMapper userMapper;

    @Test
    public void contextLoads() {}
//
//        JSONArray jsonArray = new JSONArray();
//        for(int i=0;i<30;i++){
//
////            Long beginTime = Calendar.getInstance().getTimeInMillis();
////            Long endTime = Calendar.getInstance().getTimeInMillis();
////            logger.info(i + " spend time : " + (endTime - beginTime) / 1000 + "s" + (endTime - beginTime) % 1000 + "ms");
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("_id", i);
//            jsonArray.add(jsonObject);
//        }
//        mongoTemplate.save(jsonArray,);
//
//
////		User user = userMapper.selectByPrimaryKey("aaa");
////		user = null;
//
////		Update update = new Update();
////		TeamPlayerInfo teamPlayerInfo = mongoTemplate.findOne(Query.query(Criteria.where("teamId").is("3e0ad95e0e7f41b4a3e30bc3126fddd5")),
////				TeamPlayerInfo.class,"teamPlayerInfo");
////		JSONObject json = new JSONObject();
////		json.put("homeOwner","owner");
////		UpdateResult result = mongoTemplate.updateMulti(Query.query(Criteria.where("teamId").is("3e0ad95e0e7f41b4a3e30bc3126fddd5")),
////				update.pull("playerArrayList",json ),
////				TeamPlayerInfo.class,"teamPlayerInfo");
////		System.out.println(result.getModifiedCount());
//    }

}
