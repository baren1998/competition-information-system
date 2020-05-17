package com.lxc.Competitioninformationsystem.service;

import com.lxc.Competitioninformationsystem.entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;

public interface CompetitionService {
    Competition saveCompetition(Competition competition);
    List<Competition> saveAllCompetitions(List<Competition> competitions);
    void deleteCompetition(Integer id);
    Competition findCompetitionById(Integer id);
    List<Competition> findAllCompetitions();
    List<Competition> findAllCompetitionsByIds(Iterable<Integer> ids);
    List<Competition> findCompetitionsByType(String raceType);
    Page<Competition> findCompetitionsByPage(Pageable pageable);
    Page<Competition> findCompetitionsByTypeAndPage(int raceType, Pageable pageable);
    Competition queryCompetition(String raceName);
    int updateCompetition(int id, int teamNum, Timestamp endTime, Timestamp updateTime);
}
