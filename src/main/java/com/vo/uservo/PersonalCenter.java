package com.vo.uservo;

import lombok.Data;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/4/21 15:23
 */
@Data
public class PersonalCenter {

    private String username;    //用户名，作为唯一标识
    private String identity;    //用户的身份
    private String email;  //用户的邮箱
    private String description;//用户自我描述
    private String[] tags;//用户的标签，对什么类型的项目感兴趣
    private double credits;//用户的积分值
    private double experience;//用户的经验值
    private int rank;//用户的排名
    private double rankRatio;//用户当前排名超过多少用户
    private double quality;//质量衡量

    private int numRelease;//用户发包数
    private int numContract;//用户承包数

}
