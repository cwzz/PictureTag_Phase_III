package com.vo.uservo;

import com.enums.ProjectType;
import com.model.Project;
import lombok.Data;

import java.util.Map;

@Data
public class UserStatistics {

    private String username;
    private int num_Release;
    private int num_Contract;
    private int rank;//群体排名，根据经验值
    private double experience;
    private double generalQuality;//用户的各个种类的标注质量
    private Map<String,Integer> gongXian;//用户的标注结果中贡献率为0-20%，20%-40%,40%-60%,60%-80%,80%-100%
    private double activeDegreeRelease;//用户的发包活跃度,根据在线时长和发布项目数决定
    private double activeDegreeContract;//用户的承包活跃度，根据在线时长和发布项目数决定

    private Map<String,Integer> contractPerState;//用户承包的处在各个状态的项目数
    private Map<ProjectType,Integer> contractPerType;//用户承包的处在各个类别的项目数
    private Map<String,Integer> releasePerState;//用户发布的处在各个状态的项目数
    private Map<ProjectType,Integer> releasePerType;//用户发布的处在各个类别的项目数


    //对于承包者的统计数据
    private Map<ProjectType,String> ChanChuBiPerType;//用户在不同类别的项目投入产出比
    private Map<ProjectType,String> ChanChuBiByCredits;//用户在给定项目的积分上的投入产出比

    private Map<ProjectType,String> gongxianPerType;//用户在各个类别的贡献率(就是之前写的quality)
    private Map<ProjectType,String> gongxianPerTypeAllUser;//整个系统的用户在各个类别的贡献率

    //用户在一定时间内的完成的项目贡献率如何，比如<30分钟完成的项目平均贡献率是多少，30—60分钟完成的项目平均贡献率是多少
    private Map<String,String> gongxianAndTime;
    private Map<String,String> gongxianAndTimeAllUser;

    //对于发布项目的统计数据
    private Map<Double,Integer> creditsAndNumAnimal;//用户发布多少积分对应会有多少人来承包，可以画散点图

}
