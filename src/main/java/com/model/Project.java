package com.model;

import com.enums.ProjectState;
import com.enums.ProjectType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="project")
@Data
public class Project {

    @Id
    @Column(name="pid")
    private String pro_ID;
    @Column(name="pro_name")
    private String pro_name;
    @Column(name="points")
    private double points;//积分
    @Column(name="brief_intro")
    private String brief_intro;//项目简介

    @Column(name="worker_list")
    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private Set<String> workerList;//工作人列表
    @Column(name="releaseTime")
    private Date releaseTime;//发布时间
    @Column(name = "deadLine")
    private Date deadLine;//截止时间
    private int remainTime;//剩余时间
    @Column(name = "pro_type")
    private ProjectType pro_type;
    @Column(name = "pro_state")
    private ProjectState pro_state;
    @Column(name = "pro_requester")
    private String pro_requester;//发起者ID
    @Column(name = "detailRequire")
    private String detailRequire;
    @Column(name = "note")
    private String note;

    //再承包的用户应该得到项目的第几组图片
    private int allocationIndex;
    private long clickNum;

    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private List<String> urls;

    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private Set<String> finished_list;
    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private List<String> combineRes_urls;//存储的是整合结果的url

    public Project(){}

    public Project(String pro_ID, String pro_name, double points, String brief_intro, Set<String> workerList,
                   Date releaseTime, Date deadLine, int remainTime, ProjectType pro_type, ProjectState pro_state,
                   String pro_requester, String detailRequire, String note, List<String> urls,Set<String> finishedList,long clickNum,int index,List<String> combineRes_urls){
        this.pro_ID=pro_ID;
        this.pro_name=pro_name;
        this.points=points;
        this.brief_intro=brief_intro;
        this.workerList=workerList;
        this.releaseTime=releaseTime;
        this.deadLine=deadLine;
        this.remainTime=remainTime;
        this.pro_type=pro_type;
        this.pro_state=pro_state;
        this.pro_requester=pro_requester;
        this.detailRequire=detailRequire;
        this.note=note;
        this.urls=urls;
        this.finished_list=finishedList;
        this.clickNum=clickNum;
        this.allocationIndex=index;
        this.combineRes_urls=combineRes_urls;
    }

}
