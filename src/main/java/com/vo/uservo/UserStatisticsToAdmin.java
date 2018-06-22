package com.vo.uservo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @Author:zhangping
 * @Description: 管理员要查看的统计信息
 * @CreateData: 2018/6/18 0:58
 */
@Data
public class UserStatisticsToAdmin {

    //当前用户总数
    //当前在线人数
    //最近一周内注册人数
    //最近一个月内注册人数
    private int totalNum;
    private int onlineNum;
    private int registerThisWeek;
    private int registerThisMonth;
    private Map<String,Integer> registerPerMonth;

    private Map<String,Integer> userNumPerTimePhase;

    public UserStatisticsToAdmin(){}
}
