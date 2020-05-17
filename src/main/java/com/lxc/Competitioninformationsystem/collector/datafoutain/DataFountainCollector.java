package com.lxc.Competitioninformationsystem.collector.datafoutain;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lxc.Competitioninformationsystem.entity.Competition;
import com.lxc.Competitioninformationsystem.utils.HttpUtils;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataFountainCollector {

    @Autowired
    private OkHttpClient client;

    private Map<String, Competition> competitionMap = new HashMap<>();

    private List<String> urlList = new ArrayList<>();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");

    private static final String BASE_URL_TEMPLATE = "https://www.datafountain.cn/competitions/%s";

    public Map<String, Competition> collectCompetitionInfo() throws Exception {
        // 获取OKHttpClient实例
//        OkHttpClient client = HttpUtils.getHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpUtils.DATA_FOUNTAIN_QUERY_URL).newBuilder();
        urlBuilder.addQueryParameter("state", "in_service");
        urlBuilder.addQueryParameter("page", "1");
        urlBuilder.addQueryParameter("per_page", "50");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if(response != null) {
            String json = response.body().string();
            handleJSON(json);
        }
        return competitionMap;
    }

    private void handleJSON(String json) throws Exception {
        JsonObject rootObject = new Gson().fromJson(json, JsonObject.class);
        JsonObject cmptObject = rootObject.getAsJsonObject("cmpt");
        JsonArray competitonArray = cmptObject.getAsJsonArray("competitions");
        for(int i = 0; i < competitonArray.size(); i++) {
            JsonObject singleRaceObject = competitonArray.get(i).getAsJsonObject();

            Competition competition = new Competition();

            // 获取比赛id
            String raceId = singleRaceObject.get("id").getAsString();

            // 设置比赛开始时间
            String startTimeStr = singleRaceObject.get("startTime").getAsString().replace("Z", " UTC");
            Timestamp startTime = new Timestamp(dateFormat.parse(startTimeStr).getTime());
            competition.setStartTime(startTime);

            // 设置比赛结束时间，若比赛结束时间小于当前时间，则不添加该场比赛
            String endTimeStr = singleRaceObject.get("endTime").getAsString().replace("Z", " UTC");
            Timestamp endTime = new Timestamp(dateFormat.parse(endTimeStr).getTime());
            Timestamp currentTime = new Timestamp(new Date().getTime());
            if(endTime.before(currentTime)) {
                continue;
            }
            competition.setEndTime(endTime);

            // 设置比赛标签
            String raceTypeStr = singleRaceObject.get("typeLabel").getAsString();
            int raceType;
            if(raceTypeStr.equals("方案赛")) {
                raceType = 2;
            } else {
                raceType = 1;
            }
            competition.setType(raceType);
            competition.setTag(raceTypeStr);

            // 设置比赛奖励
            String reward = singleRaceObject.get("reward").getAsString();
            competition.setReward(reward);

            competitionMap.put(raceId, competition);
            urlList.add(String.format(BASE_URL_TEMPLATE, raceId));
        }
    }

    public List<String> getRequestUrls() {
        return urlList;
    }
}
