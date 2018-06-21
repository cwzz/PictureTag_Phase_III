package com.vo.uservo;

import com.enums.ProjectType;
import lombok.Data;

import java.util.Map;

@Data
public class UserStatistics {

    private String username;
    private int num_Release;
    private int num_Contract;
    private int rank;//群体排名，根据经验值
    private double experience;
    private Map<ProjectType,Double> quality;
    private double generalQuality;//用户的各个种类的标注质量
    private Map<String,Integer> gongXian;//用户的标注结果中贡献率为0-20%，20%-40%,40%-60%,60%-80%,80%-100%
    private double activeDegreeRelease;//用户的发包活跃度,根据在线时长和发布项目数决定
    private double activeDegreeContract;//用户的承包活跃度，根据在线时长和发布项目数决定

    private Map<String,Integer> contractPerState;//用户承包的处在各个状态的项目数
    private Map<ProjectType,Integer> contractPerType;//用户承包的处在各个类别的项目数
    private Map<String,Integer> releasePerState;//用户发布的处在各个状态的项目数
    private Map<ProjectType,Integer> releasePerType;//用户发布的处在各个类别的项目数

}
