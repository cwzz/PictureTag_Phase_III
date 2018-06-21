package com.vo.uservo;

import com.enums.ProjectState;
import com.enums.ProjectType;
import lombok.Data;

@Data
public class ProBriefInfo {
    private String pid;
    private String name;
    private String briefInfor;
    private ProjectState state;
    private ProjectType type;
    private int numOfWorker;
    private int remainTime;
    private double points;
    private double finalGet;

    public ProBriefInfo(String pid, String name, String briefInfor, ProjectState state,ProjectType type,int num_Worker,int remainTime,double points){
        this.state=state;
        this.pid=pid;
        this.name=name;
        this.briefInfor=briefInfor;
        this.type=type;
        this.numOfWorker=num_Worker;
        this.remainTime=remainTime;
        this.points=points;
        this.finalGet=0;
    }
}
