package com.blservice;

import com.enums.ProjectType;
import com.enums.ResultMessage;
import com.model.BrowseRecord;
import com.vo.uservo.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


/**
 * @Author:zhangping
 * @Description: 用户操作接口类，包括注册、登陆、登出、以及显示用户列表
 * @CreateData: 2018/3/29 11:27
 */
@Transactional
public interface UserBLService {

    //注册
    ResultMessage register(NewUser newUser);

    //登陆
    ResultMessage login(LoginReq loginReq);

    //登出
    ResultMessage logout(String username);

    //修改个人信息
    ResultMessage reset(PersonalCenter user);

    //修改密码
    ResultMessage resetPassword(String username, String oldPass, String newPass);

    PersonalCenter getPersonal(String username);

    ReleaseProject getRelease(String username);

    ContractProject getContract(String username);

    UserStatistics getUserStatistics(String username);

    //得到用户的浏览记录
    Set<BrowseRecord> getBrowseRecord(String username);
    //用户的浏览记录增加一条
    void insertBrowseRecord(NewBrowseRecord newBrowseRecord);

    //查询用户的积分值
    double getCredits(String username);
    //更新用户的积分值,参数为用户名，项目类型，积分改变值，项目开始工作时间
    ResultMessage updateCredits(String username, ProjectType type,double totalCredit, double dValue,Date startTime);
    //更新用户的经验值
    ResultMessage updateExperience(String username, double dValue);
    //更新用户的质量指标
    ResultMessage updateQuality(String username, ProjectType type, double gongXian,Date startTime);

    //用户发布新的项目
    ResultMessage NewRelease(String username, ProjectType type);
    //用户开始标注新的项目
    ResultMessage NewContract(String username, ProjectType type);

    //得到活跃发包方的列表
    ArrayList<ActiveUser> getActiveRequester();
    //得到活跃承包方的列表
    ArrayList<ActiveUser> getActiveWorker();

    //根据排名(经验值)得到用户列表
    ArrayList<String> getUserList();

    UserStatisticsToAdmin getUserStatisticsToAdmin(int year);

}
