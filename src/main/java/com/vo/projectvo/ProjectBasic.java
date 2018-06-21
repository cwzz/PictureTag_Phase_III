package com.vo.projectvo;


import com.enums.ProjectState;
import com.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectBasic {
    private String pro_ID;
    private String pro_name;
    private double accumulate_points;//积分
    private String brief_intro;//项目简介
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+08:00")
    private Date releaseTime;//发布时间
    private int remainBidTime;//剩余时间
    private ProjectType pro_type;
    private ProjectState pro_state;
    private String pro_requester;//发起者ID
    private int picNum;
    private int worker_num;




    public ProjectBasic(){}
    public ProjectBasic(String pro_ID,String pro_name,double points,String brief_intro,Date releaseTime,int remainTime,
                     ProjectType pro_type,ProjectState pro_state,String pro_requester,int picNum,int worker_num){
        this.pro_ID=pro_ID;
        this.pro_name=pro_name;
        this.accumulate_points=points;
        this.brief_intro=brief_intro;
        this.releaseTime=releaseTime;
        this.remainBidTime=remainTime;
        this.pro_type=pro_type;
        this.pro_state=pro_state;
        this.pro_requester=pro_requester;
        this.picNum=picNum;
        this.worker_num=worker_num;
    }

}
