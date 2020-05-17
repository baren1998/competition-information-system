package com.lxc.Competitioninformationsystem.config;

import com.lxc.Competitioninformationsystem.utils.HttpUtils;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@Configuration
public class OKHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        // 设置本地代理
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("localhost", 1080));
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(HttpUtils.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(HttpUtils.READ_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(HttpUtils.WRITE_TIME_OUT, TimeUnit.SECONDS)
                .proxy(proxy)
                .build();
        return client;
    }
}
