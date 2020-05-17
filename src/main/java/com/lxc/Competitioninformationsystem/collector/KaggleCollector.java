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
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Component
public class KaggleCollector {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private OkHttpClient client;

    private static final String SPONSOR = "Kaggle";

    private static final String DETAIL_PAGE_URL_TEMPLATE = "https://www.kaggle.com/c/%s";

    private List<Competition> competitions;

    public KaggleCollector() {
        competitions = new ArrayList<>();
    }

    public void collectCompetitionInfo() throws Exception {

        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), "{\"pageSize\":50,\"pageToken\":\"\",\"selector\":{\"competitionIds\":[],\"listOption\":4,\"hostSegmentIdFilter\":0,\"sortOption\":0,\"searchQuery\":\"\"}}");

        Request request = new Request.Builder()
                .url(HttpUtils.KAGGLE_QUEY_URL)
                .addHeader("__requestverificationtoken", "CfDJ8LdUzqlsSWBPr4Ce3rb9VL_Lwa8iXtIhlsOcQjbfjQ6XvLQnNRj4q1OQHV_GL5p2Lis-t2euj7f0tUixeO9EmEnMuYUQzR0hDAw4WhM-_CGrH4YX9QnYtl0mjUeGlInfKBNXyjPLDjUyy7qGSLajt98")
                .addHeader("accept", "application/json")
                .addHeader("accept-language", "zh-CN,zh;q=0.9")
                .addHeader("Content-Type", "application/json")
                .addHeader("cookie", "_ga=GA1.2.755216773.1582119201; ka_sessionid=02c2c9950a30f3b9197e2843f4fd18b615e4fb00; CSRF-TOKEN=CfDJ8LdUzqlsSWBPr4Ce3rb9VL8mPzTVgWEc5m-lpSISl0oAkizYk147rOltjSLNJSPgDv5ZanVy_zAgMd390uh-fm2xlzqQgm-O0RT-K8Ab4otspyzpacMnsJC4sKuNToamnCHh5Ho2X8ovQj1iE3kNYdw; GCLB=CK7Rr8zM1YHG7QE; XSRF-TOKEN=CfDJ8LdUzqlsSWBPr4Ce3rb9VL_Lwa8iXtIhlsOcQjbfjQ6XvLQnNRj4q1OQHV_GL5p2Lis-t2euj7f0tUixeO9EmEnMuYUQzR0hDAw4WhM-_CGrH4YX9QnYtl0mjUeGlInfKBNXyjPLDjUyy7qGSLajt98; CLIENT-TOKEN=eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJpc3MiOiJrYWdnbGUiLCJhdWQiOiJjbGllbnQiLCJzdWIiOm51bGwsIm5idCI6IjIwMjAtMDQtMjVUMTI6MjA6NTQuNTU0MjYyMVoiLCJpYXQiOiIyMDIwLTA0LTI1VDEyOjIwOjU0LjU1NDI2MjFaIiwianRpIjoiNTFiYmY0NjEtYWJjNS00ZmVkLTg0MDMtZTQwYmZjYTM3ZmFmIiwiZXhwIjoiMjAyMC0wNS0yNVQxMjoyMDo1NC41NTQyNjIxWiIsImFub24iOnRydWUsImZmIjpbIkZsZXhpYmxlR3B1IiwiS2VybmVsc0ludGVybmV0IiwiRGF0YUV4cGxvcmVyVjIiLCJEYXRhU291cmNlU2VsZWN0b3JWMiIsIktlcm5lbHNWaWV3ZXJJbm5lclRhYmxlT2ZDb250ZW50cyIsIkZvcnVtV2F0Y2hEZXByZWNhdGVkIiwiTmV3S2VybmVsV2VsY29tZSIsIk1kZUltYWdlVXBsb2FkZXIiLCJLZXJuZWxzUXVpY2tWZXJzaW9ucyIsIkRpc2FibGVDdXN0b21QYWNrYWdlcyIsIlBpbk9yaWdpbmFsRG9ja2VyVmVyc2lvbiIsIlBob25lVmVyaWZ5Rm9yR3B1IiwiQ2xvdWRTZXJ2aWNlc0tlcm5lbEludGVnIiwiVXNlclNlY3JldHNLZXJuZWxJbnRlZyIsIk5hdmlnYXRpb25SZWRlc2lnbiIsIktlcm5lbHNTbmlwcGV0cyIsIktlcm5lbFdlbGNvbWVMb2FkRnJvbVVybCIsIlRwdUtlcm5lbEludGVnIiwiS2VybmVsc0ZpcmViYXNlUHJveHkiLCJLZXJuZWxzRmlyZWJhc2VMb25nUG9sbGluZyIsIkRhdGFzZXRMaXZlTW91bnQiXX0.; _gid=GA1.2.1091435141.1587817263; _gat_gtag_UA_12629138_1=1")
                .addHeader("origin", "https://www.kaggle.com")
                .addHeader("referer", "https://www.kaggle.com/competitions")
                .addHeader("sec-fetch-mode", "cors")
                .addHeader("sec-fetch-site", "same-origin")
                .addHeader("sec-fetch-dest", "empty")
                .addHeader("x-xsrf-token", "CfDJ8LdUzqlsSWBPr4Ce3rb9VL_Lwa8iXtIhlsOcQjbfjQ6XvLQnNRj4q1OQHV_GL5p2Lis-t2euj7f0tUixeO9EmEnMuYUQzR0hDAw4WhM-_CGrH4YX9QnYtl0mjUeGlInfKBNXyjPLDjUyy7qGSLajt98")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36")
                .post(requestBody)
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
        JsonObject rootObj = new Gson().fromJson(json, JsonObject.class);
        Boolean wasSuccessful = rootObj.get("wasSuccessful").getAsBoolean();
        if(!wasSuccessful) {
            throw new Exception("ERROR======>请求数据失败！");
        }
        JsonObject resultObj = rootObj.getAsJsonObject("result");
        JsonArray cmptArray = resultObj.getAsJsonArray("competitions");

        for(int i = 0; i < cmptArray.size(); i++) {
            JsonObject singleCmptObj = cmptArray.get(i).getAsJsonObject();

            Competition competition = new Competition();

            // 设置比赛标题
            String cmptName = singleCmptObj.get("title").getAsString();
            competition.setRaceName(cmptName);

            // 设置比赛详情页面Url
            String tempStr = singleCmptObj.get("competitionName").getAsString();
            String detailPageUrl = String.format(DETAIL_PAGE_URL_TEMPLATE, tempStr);
            competition.setDetailPageUrl(detailPageUrl);

            // 设置比赛简介
            String biref = singleCmptObj.get("briefDescription").getAsString();
            competition.setBrief(biref);

            // 设置举办方
            competition.setSponsorType(2);
            competition.setSponsor(SPONSOR);

            // 设置比赛类型
            competition.setType(1);

            // 设置比赛标签
            JsonArray categories = singleCmptObj.getAsJsonArray("categories");
            if(categories.size() == 0) {
                competition.setTag("Algorithm");
            } else {
                JsonObject firstCategoryObj = categories.get(0).getAsJsonObject();
                String tag = firstCategoryObj.get("name").getAsString();
                competition.setTag(tag);
            }

            // 设置IconUrl
            String iconUrl = singleCmptObj.get("thumbnailImageUrl").getAsString();
            if(iconUrl == null || iconUrl.equals("")) {
                // 设置缺省图片
                iconUrl = "https://storage.googleapis.com/kaggle-competitions/kaggle/17651/logos/thumb76_76.png?t=2019-11-27-22-51-11";
            }
            competition.setIconUrl(iconUrl);

            // 设置参赛队伍数量
            int teamNum = singleCmptObj.get("totalTeams").getAsInt();
            competition.setTeamNum(teamNum);

            // 设置数据来源类型为2-API
            competition.setDataSourceType(2);

            // 设置奖励
            int rewardType = singleCmptObj.get("rewardTypeId").getAsInt();
            String reward;
            // 如果rewardType为1，则说明奖励为金钱，否则为其他
            if(rewardType == 1) {
                reward = "$" + singleCmptObj.get("rewardQuantity").getAsString();
            } else {
                reward = singleCmptObj.get("rewardTypeName").getAsString();
                // 如果为空，则设置缺省奖励数据
                if(reward.equals("")) {
                    reward = "Prizes";
                }
            }
            competition.setReward(reward);

            // 设置比赛开始时间
            JsonObject enabledTimeObj = singleCmptObj.getAsJsonObject("dateEnabled");
            long enabledTimeSeconds = enabledTimeObj.get("seconds").getAsLong();
            int enabledTimeNanos = enabledTimeObj.get("nanos").getAsInt();
            LocalDateTime enabledTime = LocalDateTime.ofEpochSecond(enabledTimeSeconds, enabledTimeNanos, ZoneOffset.UTC);
            Timestamp startTime = Timestamp.valueOf(enabledTime);
            competition.setStartTime(startTime);

            // 设置比赛结束时间
            JsonObject deadlineObj = singleCmptObj.getAsJsonObject("deadline");
            long deadlineSeconds = deadlineObj.get("seconds").getAsLong();
            int deadlineNanos = deadlineObj.get("nanos").getAsInt();
            LocalDateTime deadline = LocalDateTime.ofEpochSecond(deadlineSeconds, deadlineNanos, ZoneOffset.UTC);
            Timestamp endTime = Timestamp.valueOf(deadline);
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
