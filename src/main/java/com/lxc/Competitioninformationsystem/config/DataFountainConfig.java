package com.lxc.Competitioninformationsystem.config;

import com.lxc.Competitioninformationsystem.collector.datafoutain.DataFountainCollector;
import com.lxc.Competitioninformationsystem.collector.datafoutain.DataFountainPageProcessor;
import com.lxc.Competitioninformationsystem.entity.Competition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class DataFountainConfig {

    @Autowired
    DataFountainCollector collector;

    @Bean
    public DataFountainPageProcessor dataFountainPageProcessor() throws Exception {
//        DataFountainCollector collector = new DataFountainCollector();
        Map<String, Competition> competitionMap = collector.collectCompetitionInfo();
        List<String> requestUrls = collector.getRequestUrls();
        return new DataFountainPageProcessor(competitionMap, requestUrls);
    }
}
