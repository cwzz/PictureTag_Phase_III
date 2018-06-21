package com.dao;


import com.enums.ProjectState;
import com.enums.ProjectType;
import com.enums.SearchProState;
import com.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Table(name = "project")
@Repository
public interface ProjectDao extends JpaRepository<Project,String> {

    @Query(value = "select p from Project p where p.pro_requester=:username")
    List<Project> findByUser(@Param("username")String username);

    @Query(value = "select p from Project p where p.pro_name like %?1% or p.brief_intro like %?1%")
    ArrayList<Project> searchPro(String pro_name);

    @Query(value = "select p.pro_ID from Project p where p.pro_type=:pro_type")
    ArrayList<String> searchProByType(@Param("pro_type")ProjectType pro_type);

    @Query(value = "select p.pro_ID from Project p where p.pro_state=:pro_state")
    ArrayList<String> searchProByState(@Param("pro_state")SearchProState pro_state);

    @Query(value = "select p.pro_ID from Project p where p.points between ?1 and ?2")
    ArrayList<String> searchProByPointRange(int min,int max);

    @Query(value = "select p.pro_ID from Project p where p.remainTime between ?1 and ?2")
    ArrayList<String> searchProByNumberOfDays(int min,int max);

    @Modifying
    @Transactional
    @Query(value = "update Project as p set p.clickNum=p.clickNum+1 where p.pro_ID=:pro_ID")
    void addClickNum(@Param("pro_ID")String pro_ID);

    //简介的字数
    @Query(value = "select LENGTH(p.brief_intro)/3 from Project p where p.pro_ID=:pro_ID")
    int briefIntroNum(@Param("pro_ID")String pro_ID);

    //项目名字的字数
    @Query(value = "select LENGTH(p.pro_name)/3 from Project p where p.pro_ID=:pro_ID")
    int nameNum(@Param("pro_ID")String pro_ID);

    //项目中简介的平均字数
    @Query(value = "select avg((length(p.brief_intro))/3) from Project p where p.pro_ID in :pro_ID")
    double avgBriefIntroNum(@Param("pro_ID")ArrayList<String> pro_ID);

    //项目中名字的平均字数
    @Query(value = "select avg((length(p.pro_name))/3) from Project p where p.pro_ID in :pro_ID")
    double avgnameNum(@Param("pro_ID")ArrayList<String> pro_ID);

    //项目名字中出现过关键词的个数
    @Query(value = "select count(p) from Project p where p.pro_name like %?1%")
    int countNameNum(String keywords);

    //项目简介中出现过关键词的个数
    @Query(value = "select count(p) from Project p where p.brief_intro like %?1%")
    int countBriefIntroNum(String keywords);

    //一共发布了多少项目
    @Query(value = "select count(p) from Project p")
    int sum();

    //项目处于已完成状态的个数
    @Query(value = "select count(p) from Project p where p.pro_state=:state")
    int finishNum(@Param("state")ProjectState projectState);

}
