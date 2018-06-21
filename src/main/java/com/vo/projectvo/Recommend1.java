package com.vo.projectvo;

import com.model.Project;
import lombok.Data;

@Data
public class Recommend1 {
    private String pid;
    private String name;
    private String brief_intro;
    private double points;
    private int workerNum;
    private int remainTime;

    public Recommend1(){}

    public Recommend1(Project project){
        this.pid=project.getPro_ID();
        this.name=project.getPro_name();
        this.brief_intro=project.getBrief_intro();
        this.points=project.getPoints();
        this.workerNum=project.getWorkerList().size();
        this.remainTime=project.getRemainTime();
    }


}
