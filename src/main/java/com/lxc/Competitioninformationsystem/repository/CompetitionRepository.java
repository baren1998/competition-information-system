package com.lxc.Competitioninformationsystem.repository;

import com.lxc.Competitioninformationsystem.entity.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, Integer> {

    @Query(value = "SELECT * FROM competition WHERE type = :type", nativeQuery = true)
    List<Competition> findCompetitionsByType(@Param("type") String raceType);

    // 支持分页
    @Query(value = "SELECT * FROM competition WHERE type = :type",
            countQuery = "SELECT count(*) FROM competition WHERE type = :type",
            nativeQuery = true)
    Page<Competition> findCompetitionsByTypeAndPage(@Param("type") int raceType, Pageable pageable);

    // 查询比赛信息是否已经存在
    @Query(value = "SELECT * FROM competition WHERE race_name = :name", nativeQuery = true)
    Competition queryCompetition(@Param("name") String cmptName);

    // 更新比赛信息
    @Modifying
    @Query(value = "UPDATE competition SET team_num = :teamNum, end_time = :endTime, update_time = :updateTime WHERE id = :id", nativeQuery = true)
    int updateCompetition(@Param("id") int id, @Param("teamNum") int teamNum,
                          @Param("endTime") Timestamp endTime, @Param("updateTime") Timestamp updateTime);
}
