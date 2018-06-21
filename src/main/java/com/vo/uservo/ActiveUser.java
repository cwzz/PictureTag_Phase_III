package com.vo.uservo;

import lombok.Data;

/**
 * @Author:zhangping
 * @Description: 用户活跃榜显示的用户信息
 * @CreateData: 2018/4/26 22:55
 */
@Data
public class ActiveUser {
    //活跃榜属性 自我描述，活跃度，排名，好评率
    private String username;
    private String description;
    private String quality;
    private String activeDegree;//用户的活跃度,根据在线时长和项目数决定

    public ActiveUser(){}
    public ActiveUser(String username,String description,String quality,String degree){
        this.username=username;
        this.description=description;
        this.quality=quality;
        this.activeDegree=degree;
    }
}
