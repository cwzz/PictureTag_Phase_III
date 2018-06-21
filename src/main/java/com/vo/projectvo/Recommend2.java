package com.vo.projectvo;

import com.model.Project;
import lombok.Data;

@Data
public class Recommend2 {
    private String pid;
    private String brief_intro;
    private double points;

    public Recommend2(){}

    public Recommend2(Project project){
        this.pid=project.getPro_ID();
        this.brief_intro=project.getBrief_intro();
        this.points=project.getPoints();
    }
}
