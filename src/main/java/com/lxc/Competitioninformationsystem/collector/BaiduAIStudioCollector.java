package com.lxc.Competitioninformationsystem.collector;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lxc.Competitioninformationsystem.entity.Competition;
import com.lxc.Competitioninformationsystem.service.CompetitionService;
import com.lxc.Competitioninformationsystem.utils.HttpUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class BaiduAIStudioCollector {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    OkHttpClient client;

    private static final String BASE_DETAIL_URL = "https://aistudio.baidu.com/aistudio/competition/detail/%s";

    private static final String SPONSOR = "百度AI Studio飞桨大赛";

    private SimpleDateFormat dateFormat;

    private List<Competition> competitions;

    public BaiduAIStudioCollector() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        competitions = new ArrayList<>();
    }

    public void collectCompetitionInfo() throws Exception {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpUtils.BAIDU_AISTUDIO_QUERY_URL).newBuilder();
        urlBuilder.addQueryParameter("matchStatus", "1");
        urlBuilder.addQueryParameter("pageSize", "50");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response != null) {
                    String json = response.body().string();
                    try {
                        handleJSON(json);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleJSON(String json) throws Exception {
        JsonObject rootObject = new Gson().fromJson(json, JsonObject.class);
        JsonObject resultObj = rootObject.getAsJsonObject("result");
        JsonArray dataArray = resultObj.getAsJsonArray("data");

        for(int i = 0; i < dataArray.size(); i++) {
            JsonObject singleRaceObj = dataArray.get(i).getAsJsonObject();

            Competition competition = new Competition();

            // 获取比赛id后设置详情页面url
            String raceId = singleRaceObj.get("id").getAsString();
            String detailPageUurl = String.format(BASE_DETAIL_URL, raceId);
            competition.setDetailPageUrl(detailPageUurl);

            // 设置比赛名称
            String raceName = singleRaceObj.get("matchName").getAsString();
            competition.setRaceName(raceName);

            // 设置举办方
            competition.setSponsorType(2);
            competition.setSponsor(SPONSOR);

            // 设置比赛简介
            String brief = singleRaceObj.get("matchAbs").getAsString();
            competition.setBrief(brief);

            // 设置比赛类型
            competition.setType(1);
            String raceTag = singleRaceObj.get("tags").getAsString();
            competition.setTag(raceTag);

            // 设置比赛奖励
            String reward = singleRaceObj.get("reward").getAsString();
            competition.setReward(reward);

            // 设置数据来源类型为2-API
            competition.setDataSourceType(2);

            // 设置参赛队伍数量
            int teamNum = singleRaceObj.get("signupCount").getAsInt();
            competition.setTeamNum(teamNum);

            // 设置iconUrl
            String iconUrl = singleRaceObj.get("logo").getAsString();
            competition.setIconUrl(iconUrl);

            JsonArray processList = singleRaceObj.getAsJsonArray("processList");
            JsonObject processObj = processList.get(0).getAsJsonObject();
            // 设置比赛开始时间
            String startTimeStr = processObj.get("startTime").getAsString();
            Timestamp startTime = new Timestamp(dateFormat.parse(startTimeStr).getTime());
            competition.setStartTime(startTime);

            // 设置比赛结束时间
            String endTimeStr = processObj.get("endTime").getAsString();
            Timestamp endTime = new Timestamp(dateFormat.parse(endTimeStr).getTime());
            competition.setEndTime(endTime);

            // 设置比赛信息采集时间
            Timestamp updateTime = Timestamp.valueOf(LocalDateTime.now());
            competition.setUpdateTime(updateTime);

            competitions.add(competition);
        }

        // 将比赛信息插入数据库
        competitionService.saveAllCompetitions(competitions);
    }
}
