package com.vo.uservo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/4/21 15:27
 */

@Data
public class ReleaseProject {

    private String username;//用户名
    private int num_Release;//用户发起的众包数量

    private ArrayList<ProBriefInfo> ReleaseAll;//用户发布的所有的项目
    private ArrayList<ProBriefInfo> ReleaseRecycle;//用户回收站里的项目
    private ArrayList<ProBriefInfo> ReleaseDraft;//用户发起的众包，但处于草稿状态
    private ArrayList<ProBriefInfo> ReleaseOn;//用户发起的正在进行中的项目
    private ArrayList<ProBriefInfo> ReleaseExamine;//评估中
    private ArrayList<ProBriefInfo> ReleaseFinished;//用户发起的项目，且全部阶段结束

    public ReleaseProject(){}
    public ReleaseProject(String username, int num, ArrayList<ProBriefInfo> all, ArrayList<ProBriefInfo> recycle, ArrayList<ProBriefInfo> draft,
                          ArrayList<ProBriefInfo> on, ArrayList<ProBriefInfo> examine, ArrayList<ProBriefInfo> finish){
        this.username=username;
        this.num_Release=num;
        this.ReleaseAll=all;
        this.ReleaseDraft=draft;
        this.ReleaseRecycle=recycle;
        this.ReleaseOn=on;
        this.ReleaseExamine=examine;
        this.ReleaseFinished=finish;
    }

}
