package com.lxc.Competitioninformationsystem.collector.datafoutain;

import com.lxc.Competitioninformationsystem.entity.Competition;
import org.commonmark.node.*;
import org.commonmark.renderer.text.TextContentRenderer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyVisitor extends AbstractVisitor {

    private Competition competition;

    private TextContentRenderer renderer;

    private StringBuilder builder;

    public MyVisitor(Competition competition) {
        this.competition = competition;
        renderer = TextContentRenderer.builder().build();
        builder = new StringBuilder();
    }

    @Override
    public void visit(Heading heading) {

        if(heading.getFirstChild() instanceof Text) {

            Text text = (Text) heading.getFirstChild();

            // 设置比赛简介
            if(text.getLiteral().contains("介绍") || text.getLiteral().contains("简介")) {
                String brief = getNodeText(heading);
                competition.setBrief(brief);
            }

            // 设置赛题任务
//            if(text.getLiteral().contains("赛题任务")) {
//                String task = getNodeText(heading);
//                competition.setTask(task);
//            }

            // 设置参赛规则
//            if(text.getLiteral().contains("参赛规则")) {
//                Node node = heading.getNext();
//                if(node instanceof Paragraph || node instanceof BulletList) {
//                    builder.append(renderer.render(node));
//                    String rule = builder.toString();
//                    competition.setRule(rule);
//                }
//            }

            // 设置比赛赛程
//            if(text.getLiteral().contains("赛题赛程")) {
//                Node node = heading.getNext();
//                while (node instanceof Paragraph || node instanceof BulletList || node instanceof HtmlBlock) {
//                    if(node instanceof Paragraph) {
//                        Node childNode = node.getFirstChild();
//                        Text textNode;
//                        if(childNode instanceof StrongEmphasis) {
//                            textNode = (Text) childNode.getFirstChild();
//                        } else {
//                            textNode = (Text) childNode;
//                        }
//                        builder.append(textNode.getLiteral());
//                        builder.append("\n");
//                    }
//                    else if(node instanceof BulletList) {
//                        builder.append(renderer.render(node));
//                    }
//                    // 如果是HtmlBlock类型则调用方法解析html文本
//                    else {
//                        String htmlBlock = renderer.render(node);
//                        parseHtmlBlock(htmlBlock);
//                    }
//                    node = node.getNext();
//                }
//                String schedule = builder.toString();
//                competition.setSchedule(schedule);
//            }
        }
        clearStringBuilder();
    }

    private String getNodeText(Heading heading) {
        Node node = heading.getNext();
        while (node instanceof Paragraph || node instanceof StrongEmphasis) {
//            Text briefTextNode = (Text) node.getFirstChild();
//            builder.append(briefTextNode.getLiteral());
            String text = renderer.render(node);
            builder.append(text);
            node = node.getNext();
        }
        return builder.toString();
    }

    // 解析markdwon内嵌的html块
    private void parseHtmlBlock(String html) {
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByClass("timeline-item__content");
        for(int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            if(element.hasText()) {
                builder.append(element.text());
                builder.append("\n");
            }
        }
    }

    private void clearStringBuilder() {
        builder.delete(0, builder.length());
    }
}
