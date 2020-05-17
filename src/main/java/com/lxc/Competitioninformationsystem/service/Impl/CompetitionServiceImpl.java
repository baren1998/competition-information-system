package com.lxc.Competitioninformationsystem.service.Impl;

import com.google.gson.Gson;
import com.lxc.Competitioninformationsystem.entity.Competition;
import com.lxc.Competitioninformationsystem.entity.Notification;
import com.lxc.Competitioninformationsystem.repository.CompetitionRepository;
import com.lxc.Competitioninformationsystem.service.CompetitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    @Autowired
    private CompetitionRepository competitionRepository;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Competition saveCompetition(Competition competition) {
        Competition queryCompetition = competitionRepository.queryCompetition(competition.getRaceName());
        if(queryCompetition != null) {
            updateCompetition(queryCompetition.getId(), competition.getTeamNum(), competition.getEndTime(), competition.getUpdateTime());
        } else {
            pushNotification(competition);
            competitionRepository.save(competition);
        }
        return competition;
    }

    @Override
    public void deleteCompetition(Integer id) {
        competitionRepository.deleteById(id);
    }

    @Override
    public Competition findCompetitionById(Integer id) {
        return competitionRepository.findById(id).get();
    }

    @Override
    public List<Competition> findAllCompetitions() {
        return competitionRepository.findAll();
    }

    @Override
    public List<Competition> saveAllCompetitions(List<Competition> competitions) {
        // 在添加之前先判断是否数据已经存在
        competitions.forEach(value -> {
            Competition competition = queryCompetition(value.getRaceName());
            if(competition != null) {
                // 如果查询结果不为空，说明该条比赛信息已经存在，更新数据库信息即可
                int id = competition.getId();
                updateCompetition(id, value.getTeamNum(), value.getEndTime(), value.getUpdateTime());
            } else {
                // 如果为空则说明为新的竞赛信息，添加到数据库，并且将新通知推送到redis服务器
                competitionRepository.save(value);
                pushNotification(value);
            }
        });
        return competitions;
    }

    // 将新通知推送到Redis服务器上
    private void pushNotification(Competition competition) {
        // 生成新通知
        Notification notification = new Notification();
        notification.setCmptName(competition.getRaceName());
        notification.setBrief(competition.getBrief());
        notification.setCoverImageUrl(competition.getIconUrl());
        notification.setDetailPageUrl(competition.getDetailPageUrl());
        String notifyTime = String.valueOf(LocalDateTime.now());
        notification.setNotifyTime(notifyTime);

        // 将新通知追加到redis列表中
//        redisTemplate.opsForList().leftPush("notification", notification);
        String message = new Gson().toJson(notification);
        

        // 更新最后一次推送通知的时间
        String lastNotifyTimeMillis = String.valueOf(LocalDateTime.now().toString());
        stringRedisTemplate.opsForValue().set("lastNotify", lastNotifyTimeMillis);
    }

    @Override
    public List<Competition> findAllCompetitionsByIds(Iterable<Integer> ids) {
        return competitionRepository.findAllById(ids);
    }

    @Override
    public List<Competition> findCompetitionsByType(String raceType) {
        return competitionRepository.findCompetitionsByType(raceType);
    }

    @Override
    public Page<Competition> findCompetitionsByPage(Pageable pageable) {
        return competitionRepository.findAll(pageable);
    }

    @Override
    public Page<Competition> findCompetitionsByTypeAndPage(int raceType, Pageable pageable) {
        return  competitionRepository.findCompetitionsByTypeAndPage(raceType, pageable);
    }

    @Override
    public Competition queryCompetition(String raceName) {
        return competitionRepository.queryCompetition(raceName);
    }

    @Override
    public int updateCompetition(int id, int teamNum, Timestamp endTime, Timestamp updateTime) {
        return competitionRepository.updateCompetition(id, teamNum, endTime, updateTime);
    }
}
