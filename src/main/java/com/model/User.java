package com.model;

import com.bl.Constant;
import com.enums.ProjectType;
import com.enums.UserIdentity;
import com.vo.uservo.NewUser;
import lombok.Data;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table(name = "user")
public class User {

    private static final double iniCredits=100;

    @Id
    @Column(name = "uid")
    private String username;    //用户名，作为唯一标识

    private String password;    //用户登陆的密码

    private UserIdentity identity;    //用户的身份

    private String email;  //用户的邮箱

    private String description;//用户自我描述

    private boolean isOn;    //用户当前是否处于登陆状态

    @OneToMany(cascade = CascadeType.ALL)
    private Set<BrowseRecord> records ;//用户的浏览记录
    //统计数据
    private Date dateRegister;//用户注册时间
    private Date dateLogin;//用户最后一次登陆时间
    private Date dateLogout;//用户最后一次登出时间
    private long totalDuration;//用户在线总时长，以分钟为单位

    private String tag;//用户的标签，主要指工人，他们擅长的或者感兴趣的项目类型
    private double generalQuality;//综合工人的项目完成情况，作为其质量的衡量标准
    private double credits;//用户的积分值
    private double experience;//用户的经验值，由打星及项目经历决定，当有用户承包或发布项目时，经验值都会相应增加

    @ElementCollection//(targetClass = Double.class,fetch = FetchType.LAZY)
    private Map<ProjectType,Double> quality;//工人各个分类的项目的标注质量
    @ElementCollection//(targetClass = String.class,fetch = FetchType.LAZY)
    private Map<Integer,Integer> gongXian;//工人各个贡献率阶段的数量，0-20%，20%-40%……
    @ElementCollection
    private Map<ProjectType,Integer> contractTypeNum;//用户承包的各个种类的数量
    @ElementCollection//(targetClass = Integer.class,fetch = FetchType.LAZY)
    private Map<ProjectType,Integer> releaseTypeNum;//用户承包的各个种类的数量

    private double activeRelease;//用户的发包活跃度,根据在线时长和发布项目数决定
    private double activeContract;//用户的承包活跃度，根据在线时长和发布项目数决定

    private int num_Release;//用户总发包数
    private int num_Contract;//用户总承包数

    public User(){}

    public User(NewUser newUser) {
        this.username=newUser.getUsername();
        this.password=newUser.getPassword();
        this.email=newUser.getEmail();
        this.identity= UserIdentity.COMMONUSER;
        this.description="";
        this.isOn=false;
        this.tag="All";
        this.credits=iniCredits;
        this.experience=0;

        this.quality=new HashMap<>();
        this.gongXian=new HashMap<>();
        this.contractTypeNum=new HashMap<>();
        this.releaseTypeNum=new HashMap<>();

        ProjectType[] types= Constant.Types;

        for(int i=0;i<types.length;i++){
            quality.put(types[i],0.0);
            contractTypeNum.put(types[i],0);
            releaseTypeNum.put(types[i],0);
            gongXian.put(i,0);
        }

        this.generalQuality=0;
        this.activeContract=0;
        this.activeRelease=0;
        this.records=new HashSet<>();
        this.num_Contract=0;
        this.num_Release=0;

        this.dateRegister=new Date();
        this.totalDuration=0;
    }

}
