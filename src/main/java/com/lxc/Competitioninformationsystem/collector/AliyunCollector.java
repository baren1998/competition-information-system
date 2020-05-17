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

import javax.persistence.Column;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class AliyunCollector {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    OkHttpClient client;

    private static final String BASE_URL = "https://tianchi.aliyun.com/competition/entrance/%s/introduction";

    private SimpleDateFormat dateFormat;

    private List<Competition> competitions;

    private StringBuilder builder;

    public AliyunCollector() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        competitions = new CopyOnWriteArrayList<>();
        builder = new StringBuilder();
    }

    public void collectCompetitionInfo() throws Exception {

        int pageNum = getPageNum(client);
        if(pageNum == 0 || pageNum == -1) {
            throw new Exception("Get Aliyun-tianchi competitions failed");
        }
        // 根据分页数发送Http请求并且处理返回的数据
        // 此处开启了多个线程处理数据，因此list应支持异步读写操作
        for(int i = 1; i <= pageNum; i++) {
            Request request = getRequest(i);
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
    }

    private Request getRequest(int pageNum) {
        // 填写Query参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse(HttpUtils.ALIYUN_TIANCHI_QUERY_URL).newBuilder();
        urlBuilder.addQueryParameter("pageNum", String.valueOf(pageNum));
        urlBuilder.addQueryParameter("state", "1");
        urlBuilder.addQueryParameter("pageSize", "10");

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .addHeader("x-csrf-token", "ZeZlQZzC-HCTAWSTFb9Lc0qi8CyTJwaz_7Qg")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36")
                .get()
                .build();

        return request;
    }

    // 获取活跃竞赛分页数(每页默认10条比赛信息)
    private int getPageNum(OkHttpClient client)  {
        Request request = getRequest(1);
        try {
            int pageNum = 0;
            Response response = client.newCall(request).execute();
            String json = response.body().string();

            // 将JSON字符串转换为JSON对象并且提取其中的pages字段
            JsonObject rootObject = new Gson().fromJson(json, JsonObject.class);
            JsonObject dataObject = rootObject.getAsJsonObject("data");
            String pages = dataObject.get("pages").getAsString();
            if(pages != null) {
                pageNum = Integer.parseInt(pages);
            }

            return pageNum;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 处理Http请求返回的JSON字符串
    private void handleJSON(String json) throws Exception {
        JsonObject rootObject = new Gson().fromJson(json, JsonObject.class);
        JsonObject dataObject = rootObject.getAsJsonObject("data");
        JsonArray raceArray = dataObject.getAsJsonArray("list");
        for(int i = 0; i < raceArray.size(); i++) {
            JsonObject singleRaceObject = raceArray.get(i).getAsJsonObject();

            Competition competition = new Competition();

            // 设置详情页面的Url
            String raceId = singleRaceObject.get("raceId").getAsString();
            String detailPageUrl = String.format(BASE_URL, raceId);
            competition.setDetailPageUrl(detailPageUrl);

            // 设置比赛名称
            String raceName = singleRaceObject.get("raceName").getAsString();
            competition.setRaceName(raceName);

            // 设置简介
            String brief = singleRaceObject.get("brief").getAsString();
            competition.setBrief(brief);

            // 设置参赛人数
            int teamNum = singleRaceObject.get("teamNum").getAsInt();
            competition.setTeamNum(teamNum);

            // 设置比赛奖励
            String currencySymbol = singleRaceObject.get("currencySymbol").getAsString();
            if(currencySymbol.equals("$")) {
                builder.append("$");
            } else {
                builder.append("¥");
            }
            String reward = singleRaceObject.get("bonus").getAsString();
            builder.append(reward);
            competition.setReward(builder.toString());
            builder.delete(0, builder.length());

            // 设置数据来源类型，由于是从API获取故为2
            competition.setDataSourceType(2);

            // 设置比赛类型和标签
            String raceTypeStr = singleRaceObject.get("raceType").getAsString();
            int raceType;
            switch (raceTypeStr) {
                case "GETSTART":
                    raceType = 3;
                    break;
                case "INNOVATE":
                    raceType = 2;
                    break;
                default:
                    raceType = 1;
                    break;
            }
            competition.setType(raceType);
            competition.setTag(raceTypeStr);

            // 设置比赛信息被获取的时间
            Timestamp currentTime = Timestamp.valueOf(LocalDateTime.now());
            competition.setUpdateTime(currentTime);

            // 设置比赛的开始时间
            String startTimeStr = singleRaceObject.get("currentSeasonStart").getAsString();
            Timestamp startTime = new Timestamp(dateFormat.parse(startTimeStr).getTime());
            competition.setStartTime(startTime);

            // 设置比赛的结束时间
            String endTimeStr = singleRaceObject.get("currentSeasonEnd").getAsString();
            Timestamp endTime = new Timestamp(dateFormat.parse(endTimeStr).getTime());
            competition.setEndTime(endTime);

            // 设置举办方图片Url
            competition.setSponsorType(1);
            String sponsorImageUrl = singleRaceObject.get("orgUrl").getAsString();
            competition.setSponsor(sponsorImageUrl);
            competitions.add(competition);

            // 设置缺省封面
            String coverImageUrl = "https://dss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/tam-ogel/3a381727f13291850f8e2bfbafb1236a_121_121.jpg";
            competition.setIconUrl(coverImageUrl);
        }

        // 将结果写入数据库
        competitionService.saveAllCompetitions(competitions);
        // 清空列表下次备用
        competitions.clear();
    }
}
