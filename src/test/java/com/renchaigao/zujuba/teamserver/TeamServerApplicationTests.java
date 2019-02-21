package com.renchaigao.zujuba.teamserver;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.client.result.UpdateResult;
import com.renchaigao.zujuba.dao.User;
import com.renchaigao.zujuba.dao.mapper.UserMapper;
import com.renchaigao.zujuba.mongoDB.info.team.TeamPlayerInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TeamServerApplicationTests {

	@Autowired
	MongoTemplate mongoTemplate;

	@Autowired
	UserMapper userMapper;
	@Test
	public void contextLoads() {
		User user = userMapper.selectByPrimaryKey("aaa");
		user = null;

//		Update update = new Update();
//		TeamPlayerInfo teamPlayerInfo = mongoTemplate.findOne(Query.query(Criteria.where("teamId").is("3e0ad95e0e7f41b4a3e30bc3126fddd5")),
//				TeamPlayerInfo.class,"teamPlayerInfo");
//		JSONObject json = new JSONObject();
//		json.put("homeOwner","owner");
//		UpdateResult result = mongoTemplate.updateMulti(Query.query(Criteria.where("teamId").is("3e0ad95e0e7f41b4a3e30bc3126fddd5")),
//				update.pull("playerArrayList",json ),
//				TeamPlayerInfo.class,"teamPlayerInfo");
//		System.out.println(result.getModifiedCount());
	}

}
