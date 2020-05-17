package com.lxc.Competitioninformationsystem.utils;

import okhttp3.OkHttpClient;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class HttpUtils {

    // DataFountain API
    public static final String DATA_FOUNTAIN_QUERY_URL =
            "https://www.datafountain.cn/api/competitions";

    // 阿里云天池API
    public static final String ALIYUN_TIANCHI_QUERY_URL =
            "https://tianchi.aliyun.com/competition/proxy/api/competition/api/race/listBrief";

    // 百度AI Studio API
    public static final String BAIDU_AISTUDIO_QUERY_URL =
            "https://aistudio.baidu.com/studio/match/list";

    // DataCastle API
    public static final String DATA_CASTLE_QUERY_URL =
            "https://www.pkbigdata.com/common/getNewCmptList.json";

    // Kaggle API
    public static final String KAGGLE_QUEY_URL =
            "https://www.kaggle.com/requests/CompetitionService/ListCompetitions";

    // OkHttpClient实例(单例模式)
    private static OkHttpClient client;

    // 设置连接超时时限
    public static final int CONNECT_TIME_OUT = 10;

    // 设置读取超时时限
    public static final int READ_TIME_OUT = 10;

    // 设置写入超时时限
    public static final int WRITE_TIME_OUT = 10;

    // 获取OkHttpClient实例
    public static OkHttpClient getHttpClient() {
        if(client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
        return client;
    }
}
