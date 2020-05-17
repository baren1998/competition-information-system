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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataCastleCollector {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    OkHttpClient client;

    private static final String BASE_DETAIL_URL = "https://www.pkbigdata.com/common/cmpt/%s_竞赛信息.html";

    private List<Competition> competitions;

    public DataCastleCollector() {
        competitions = new ArrayList<>();
    }

    public void collectCompetitionInfo() throws Exception {

        //　设置http get参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpUtils.DATA_CASTLE_QUERY_URL).newBuilder();
        urlBuilder.addQueryParameter("pageSize", "50");
        urlBuilder.addQueryParameter("state", "active");

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
        JsonObject dataObject = rootObject.getAsJsonObject("data");
        JsonObject cpmtListObj = dataObject.getAsJsonObject("cmptList");
        JsonArray cmptArray = cpmtListObj.getAsJsonArray("list");

        for(int i = 0; i < cmptArray.size(); i++) {
            JsonObject singleRaceObj = cmptArray.get(i).getAsJsonObject();

            Competition competition = new Competition();

            // 设置比赛名称
            String raceName = singleRaceObj.get("name").getAsString();
            competition.setRaceName(raceName);

            // 设置详情页面Url
            String detailPageUrl = singleRaceObj.get("customPage").getAsString();
            if(detailPageUrl.equals("")) {
                detailPageUrl = String.format(BASE_DETAIL_URL, raceName);
            }
            competition.setDetailPageUrl(detailPageUrl);

            // 设置比赛介绍
            String brief = singleRaceObj.get("introduction").getAsString();
            competition.setBrief(brief);

            // 设置举办方
            competition.setSponsorType(2);
            String sponsor = singleRaceObj.get("HostName").getAsString();
            if(sponsor.equals("个人")) {
                sponsor = "DataCastle数据城堡";
            }
            competition.setSponsor(sponsor);

            // 设置比赛类型
            String raceTypeStr = singleRaceObj.get("type").getAsString();
            int raceType;
            switch (raceTypeStr) {
                case "创意型":
                    raceType = 2;
                    break;
                default:
                    raceType = 1;
                    break;
            }
            competition.setType(raceType);
            competition.setTag(raceTypeStr);

            // 设置iconUrl
            String iconUrl = singleRaceObj.get("icon").getAsString();
            competition.setIconUrl(iconUrl);

            // 设置参赛队伍数量
            int teamNum = singleRaceObj.get("totalUserNumber").getAsInt();
            competition.setTeamNum(teamNum);

            // 设置数据来源类型为2-API
            competition.setDataSourceType(2);

            // 设置奖励
            String rewardStr = singleRaceObj.get("reward").getAsString();
            try {
                int reward = Integer.parseInt(rewardStr);
                competition.setReward("¥" + reward);
            } catch (NumberFormatException e) {
                competition.setReward(rewardStr);
            }

            // 设置比赛开始时间
            String startTimeStr = singleRaceObj.get("startTime").getAsString();
            long startTimeLong = Long.parseLong(startTimeStr);
            Timestamp startTime = new Timestamp(startTimeLong);
            competition.setStartTime(startTime);

            // 设置比赛结束时间
            String endTimeStr = singleRaceObj.get("endTime").getAsString();
            long endTimeLong = Long.parseLong(endTimeStr);
            Timestamp endTime = new Timestamp(endTimeLong);
            competition.setEndTime(endTime);

            // 设置比赛信息获取时间
            Timestamp updateTime = Timestamp.valueOf(LocalDateTime.now());
            competition.setUpdateTime(updateTime);

            competitions.add(competition);
        }

        // 将结果写入数据库
        competitionService.saveAllCompetitions(competitions);
    }
}
