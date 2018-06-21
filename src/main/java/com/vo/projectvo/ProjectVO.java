package com.vo.projectvo;


import com.enums.ProjectState;
import com.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ProjectVO {
    private String pro_ID;
    private String pro_name;
    private double points;//积分
    private String brief_intro;//项目简介
    private String[] workerList;//工作人列表
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+08:00")
    private Date releaseTime;//发布时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+08:00")
    private Date deadLine;//截止时间
    private int remainTime;//剩余时间
    private ProjectType pro_type;
    private ProjectState pro_state;
    private String pro_requester;//发起者ID
    private String detailRequire;
    private String note;
    private String[] urls;
    private String[] finished_list;




    public ProjectVO(){}
    public ProjectVO(String pro_ID,String pro_name,double points,String brief_intro,String[] workerList,Date releaseTime,Date deadLine,int remainTime,
                     ProjectType pro_type,ProjectState pro_state,String pro_requester,String detailRequire,String note,String[] urls,String[] finishedList){
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
    }

    public String toUrlString(){
        StringBuilder res= new StringBuilder();
        if(urls.length==0){
            res = new StringBuilder();
        }else{
            for(int i=0;i<urls.length-1;i++){
                res.append(urls[i]).append(",");
            }
            res.append(urls[urls.length - 1]);
        }
        return res.toString();
    }

    public String toWorkerString(){
        String res="";
        if(workerList.length==0){
            res="";
        }else{
            for(int i=0;i<workerList.length-1;i++){
                res=workerList[i]+",";
            }
            res=res+workerList[workerList.length-1];
        }
        return res;
    }

    public String toFinishedListString(){
        String res="";
        if(finished_list.length==0){
            res="";
        }else{
            for(int i=0;i<finished_list.length;i++){
                res=finished_list[i]+",";
            }
            res=res+finished_list[finished_list.length-1];
        }
        return res;
    }

}
