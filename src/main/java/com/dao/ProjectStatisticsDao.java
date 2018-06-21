package com.dao;

import com.model.ProjectStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Map;

@Repository
@Table(name="project_statistics")
public interface ProjectStatisticsDao extends JpaRepository<ProjectStatistics,String> {

    @Query(value = "select p from ProjectStatistics p where p.yearAndMonth like %?1%")
    ArrayList<ProjectStatistics> findInYear(String year);

    @Query(value = "select avg(p.releasedAnimalNum) from ProjectStatistics p where p.yearAndMonth like %?1%")
    double calAvgReleasedAnimalNum(String year);

    @Query(value = "select avg(p.releasedGoodsNum) from ProjectStatistics p where p.yearAndMonth like %?1%")
    double calAvgReleasedGoodsNum(String year);

    @Query(value = "select avg(p.releasedOthersNum) from ProjectStatistics p where p.yearAndMonth like %?1%")
    double calAvgReleasedOthersNum(String year);

    @Query(value = "select avg(p.releasedPersonNum) from ProjectStatistics p where p.yearAndMonth like %?1%")
    double calAvgReleasedPersonNum(String year);

    @Query(value = "select avg(p.releasedSceneNum) from ProjectStatistics p where p.yearAndMonth like %?1%")
    double calAvgReleasedSceneNum(String year);

//    @Query(value = "select p.yearAndMonth,p.registerPerMonth from ProjectStatistics p")
//    ArrayList<Map<String,Integer>> getRegisterPerMonth();
    @Query(value = "select p from ProjectStatistics p")
    ArrayList<ProjectStatistics> getRegisterPerMonth();

    @Modifying
    @Query(value = "update ProjectStatistics p set p.registerPerMonth=p.registerPerMonth+1 where p.yearAndMonth=?1")
    void newRegister(String time);



}
