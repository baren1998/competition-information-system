package com.lxc.Competitioninformationsystem.collector.datafoutain;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lxc.Competitioninformationsystem.entity.Competition;
import com.lxc.Competitioninformationsystem.service.CompetitionService;
import com.lxc.Competitioninformationsystem.utils.StringUtils;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class DataFountainPageProcessor implements PageProcessor {

    @Autowired
    private CompetitionService competitionService;

    private Map<String, Competition> competitionMap;

    private List<Competition> competitionList;

    private List<String> requestUrls;

    public DataFountainPageProcessor(Map<String, Competition> competitionMap, List<String> requestUrls) {
        this.competitionMap = competitionMap;
        this.requestUrls = requestUrls;
        this.competitionList = new ArrayList<>();
    }

    private static final String DETAIL_URL = "https://www\\.datafountain\\.cn/competitions/\\d+";

    private Site site =Site.me().setRetryTimes(3).setSleepTime(1000);

    public List<String> getRequestUrls() {
        return requestUrls;
    }

    @Override
    public void process(Page page) {
//        if(!page.getUrl().regex(DETAIL_URL).match()) {
//            List<String> tempUrls = page.getHtml()
//                    .xpath("div[@class='compt__header']/a").links().regex("/competitions/[0-9]+").all();
//            List<String> urls = tempUrls.stream()
//                    .map(value -> String.format("https://www.datafountain.cn%s", value))
//                    .collect(Collectors.toList());
//            page.addTargetRequests(urls);
//        } else { }
        if(page.getUrl().regex(DETAIL_URL).match()) {

            // 获取比赛id，然后从Map中取出对应的比赛信息实体类
            String url = page.getUrl().get();
            int index = url.lastIndexOf("/");
            String raceId = url.substring(index + 1);
            Competition competition = competitionMap.get(raceId);

            // 设置数据来源类型为1-爬虫
            competition.setDataSourceType(1);

            // 设置比赛详情页面Url
            competition.setDetailPageUrl(url);

            // 设置比赛名称
            String raceName = page.getHtml().xpath("h2[@class='competition__title']/text()").toString();
            competition.setRaceName(raceName);

            // 设置比赛举办方
            competition.setSponsorType(2);
            String sponsor = page.getHtml().xpath("div[@class='competition__sponsor']/span/text()").toString();
            competition.setSponsor(sponsor);

            // 设置比赛图片Url
            String html = page.getHtml().xpath("div[@class='competition__avatar']/html()").get();
            Document document = Jsoup.parse(html);
            Element link = document.select("img").first();
            String iconUrl = link.attr("src");
            competition.setIconUrl(iconUrl);

            // 设置参赛队伍数量
            String competition_teams = page.getHtml().xpath("div[@class='competition__teams']/b/text()").get();
            String teamNum = competition_teams.split("/")[0];
            competition.setTeamNum(Integer.parseInt(teamNum.substring(0, teamNum.length() - 1)));

            // 获取当前系统时间
            Timestamp updateTime = new Timestamp(new Date().getTime());
            competition.setUpdateTime(updateTime);

            Elements scriptElements = page.getHtml().getDocument().getElementsByTag("script");
            for (Element scriptElement : scriptElements) {
                if(isInfoScriptElement(scriptElement)) {
                    int startIndex = StringUtils.getIndex(scriptElement.toString(), '{', 2);
                    int endIndex = StringUtils.getReverseIndex(scriptElement.toString(), '}', 2);
                    String json = scriptElement.toString().substring(startIndex, endIndex + 1);

                    JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                    JsonObject stateObj = jsonObject.getAsJsonObject("state");
                    JsonObject competitionObj = stateObj.getAsJsonObject("competition");
                    JsonObject competitionInfoObj = competitionObj.getAsJsonObject("info");
                    String markdownBody = competitionInfoObj.get("cmptDescription").getAsString();
//                    System.out.println(markdownBody);

                    // 解析MarkdownBody
                    parseMarkdown(markdownBody, competition);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // 完成后将结果写入数据库中
        competitionService.saveAllCompetitions(competitionList);
        System.out.println("finalize()已触发");
        super.finalize();
    }

    @Override
    public Site getSite() {
        return site;
    }

    // 信息 script element
    private boolean isInfoScriptElement(Element scriptElement) {
        return scriptElement.toString().contains("window.__NUXT__");
    }

    // 解析Markdown文本
    private void parseMarkdown(String markdown, Competition competition) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        MyVisitor visitor = new MyVisitor(competition);
        document.accept(visitor);

//        competitionService.saveCompetition(competition);
        competitionList.add(competition);
    }
}
