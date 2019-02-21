package com.renchaigao.zujuba.teamserver.uti;

import com.renchaigao.zujuba.mongoDB.info.team.TeamInfo;

public class TeamDateFunc {

//    处理team日期，将上传的date和time处理
    public static String teamTimeFunc(String date,String time){
        String allTime = null;
        switch (time){
            case "09:00~12:00":
                allTime = date + " 09:00:00";
                break;
            case "13:00~17:00":
                allTime = date + " 13:00:00";
                break;
            case "18:00~21:00":
                allTime = date + " 18:00:00";
                break;
            case "21:00~01:00":
                allTime = date + " 21:00:00";
                break;
        }
        return allTime;
    }
}
