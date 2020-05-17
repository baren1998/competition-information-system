package com.lxc.Competitioninformationsystem.controller;

import com.lxc.Competitioninformationsystem.collector.AliyunCollector;
import com.lxc.Competitioninformationsystem.collector.BaiduAIStudioCollector;
import com.lxc.Competitioninformationsystem.collector.DataCastleCollector;
import com.lxc.Competitioninformationsystem.collector.KaggleCollector;
import com.lxc.Competitioninformationsystem.collector.datafoutain.DataFountainPageProcessor;
import com.lxc.Competitioninformationsystem.entity.Competition;
import com.lxc.Competitioninformationsystem.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.List;

@RestController
@RequestMapping("/competitions")
// 设置允许跨域
@CrossOrigin(origins = "*", maxAge = 3600)
public class CompetitionController {

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private BaiduAIStudioCollector baiduAIStudioCollector;
    @Autowired
    private DataCastleCollector dataCastleCollector;
    @Autowired
    private AliyunCollector aliyunCollector;
    @Autowired
    private DataFountainPageProcessor dataFountainPageProcessor;
    @Autowired
    private KaggleCollector kaggleCollector;

    @GetMapping("/add")
    private Competition addCompetition(Competition competition) {
       return competitionService.saveCompetition(competition);
    }

    @GetMapping("/addAll")
    private List<Competition> addAllCompetitions(List<Competition> competitions) {
        return competitionService.saveAllCompetitions(competitions);
    }

    @GetMapping("/delete")
    private String deleteCompetition(Integer id) {
        competitionService.deleteCompetition(id);
        return "SUCCESS";
    }

    @GetMapping("/query")
    private Competition queryCompetition(@RequestParam(value = "name") String name) {
        return competitionService.queryCompetition(name);
    }

    @GetMapping("/findById")
    private Competition findCompetitionById(@RequestParam(value = "id") Integer id) {
        return competitionService.findCompetitionById(id);
    }

    @GetMapping("/findByIds")
    private List<Competition> findAllCompetitionsByIds(Iterable<Integer> ids) {
        return competitionService.findAllCompetitionsByIds(ids);
    }

    @GetMapping("/findAll")
    private List<Competition> findAllCompetitions() {
        return competitionService.findAllCompetitions();
    }

    @GetMapping("/findAllByPage")
    private Page<Competition> findAllCompetitionsByPage(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageRequest = PageRequest.of(pageNum, 10, sort);
        return competitionService.findCompetitionsByPage(pageRequest);
    }

    @GetMapping("/findByType")
    private List<Competition> findAllCompetitionsByType(@RequestParam(value = "type") String raceType) {
        return competitionService.findCompetitionsByType(raceType);
    }

    @GetMapping("/findByTypeAndPage")
    private Page<Competition> findByTypeAndPage(@RequestParam(value = "type") int raceType,
                                                @RequestParam(value = "pageNum", defaultValue = "0") int pageNum) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageRequest = PageRequest.of(pageNum, 10, sort);
        return competitionService.findCompetitionsByTypeAndPage(raceType, pageRequest);
    }


    @GetMapping("/addAliyunData")
    private void addAliyunData() {
        try {
            aliyunCollector.collectCompetitionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/addBaiduData")
    private void addBaiduData() {
        try {
            baiduAIStudioCollector.collectCompetitionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/addDataCastleData")
    private void addDataCastleData() {
        try {
            dataCastleCollector.collectCompetitionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/addDataFountainData")
    private void addDataFountainData() {
        try {
            List<String> requestUrls = dataFountainPageProcessor.getRequestUrls();

            String[] urls = new String[requestUrls.size()];
            requestUrls.toArray(urls);

            Spider.create(dataFountainPageProcessor)
                    .addUrl(urls)
                    .addPipeline(new ConsolePipeline())
                    .thread(1)
                    .run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/addKaggleData")
    private void addKaggleData() {
        try {
            kaggleCollector.collectCompetitionInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
