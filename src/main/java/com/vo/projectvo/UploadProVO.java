package com.vo.projectvo;

import com.enums.ProjectType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UploadProVO {
    private String pro_ID;
    private String pro_name;
    private double points;//积分
    private String brief_intro;//项目简介
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+08:00")
    private Date deadLine;//截止时间
    private int remainTime;//剩余时间
    private ProjectType pro_type;
    private String pro_requester;//发起者ID
    private String detailRequire;
    private String note;
    private String[] urls;




    public UploadProVO(){}
    public UploadProVO(String pro_ID, String pro_name, double points, String brief_intro, Date deadLine, int remainTime,
                       ProjectType pro_type, String pro_requester, String detailRequire, String note, String[] urls){
        this.pro_ID=pro_ID;
        this.pro_name=pro_name;
        this.points=points;
        this.brief_intro=brief_intro;
        this.deadLine=deadLine;
        this.remainTime=remainTime;
        this.pro_type=pro_type;
        this.pro_requester=pro_requester;
        this.detailRequire=detailRequire;
        this.note=note;
        this.urls=urls;
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
}

