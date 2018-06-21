package com.blservice;


import com.enums.*;
import com.model.picture.Picture;
import com.vo.personaltagvo.CombineResVO;
import com.vo.projectvo.*;
import com.vo.uservo.ProBriefInfo;
import net.sf.json.JSONArray;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ProjectBLService{

    //显示项目列表
    JSONArray getProjectListForCommonUser();

    //生成项目id
    String addPro(String username);

    //保存项目
    ResultMessage savePro(UploadProVO uploadProVO);

    //上传项目
    ResultMessage uploadPro(UploadProVO uploadProVO);
    //追加积分
    ResultMessage addCredits(String username, String projectID, double credits);

    //删除项目到回收站
    ResultMessage removePro(String username, String pid);

    //从回收站彻底删除项目
    ResultMessage delPro(String username, String pid);

    //从回收站还原项目
    ResultMessage recoverPro(String username, String pid);

    //查看项目具体信息
    ProjectVO viewPro(String pid);

    //工人首次承包项目时
    ResultMessage choosePro(String workerID, String pid);
    //为工人分配下一组图片编号 图片
    int getNextGroupIndex(String projectID, String workGroup);
    String[] getNextGroupUrl(String projectID, int group);

    //工人完成标注并提交
    ResultMessage submitPro(String workerID, String pid);

    ResultMessage quitPro(String workerID, String pid);

    //工人得到的实际积分
    double getPoints(String pid, String uid);

    //存储整合结果
    ResultMessage saveCombineRes(CombineResVO combineResVO);

    //显示整合结果
    List<String> showCombineRes(String pid);
//    ArrayList<ProBriefInfo> searchProInUser(String keywords, ArrayList<ProBriefInfo> searchObject);
    ArrayList<ProjectBasic> searchPro(String keywords, ProjectType type, SearchProState state, PointsRange pointsRange, NumberOfDays numberOfDays);//模糊查找
//    ProjectStatistics calStatistics();

    //提供给用户查询到个人信息的接口
    ArrayList<ProBriefInfo> getRelease(String username);

    //提供给用户查询到的用户承包过的项目
    ProBriefInfo getBriefInfo(String pid);

    double predictPrice(int pictureNum);

    ResultMessage changeToFinish(String pid);

    ArrayList<Recommend1> recommendPro(String uid);

    ArrayList<Recommend2> recommendSimiPro(String pid, String uid);

    ArrayList<FinishCondition> showFinishConditionList(String pid);

    ArrayList<Recommend1> newestPro();

//    ArrayList<>




    //以下为项目统计信息
    //截至到现在，已完成项目总数，已发布项目总数
    int totalFinishedNum();
    int totalReleasedNum();

    //每月新发布项目数折线图,进行中(包括评估)项目总数，完成项目总数
    Map<String,Integer> releasedPerMonth(String year);
    Map<String,Integer> waitUndertakePerMonth(String year);
    Map<String,Integer> finishedPerMonth(String year);

    //各种类型每月新发布项目数
    Map<String,Integer> releasedAnimalNum(String year);
    Map<String,Integer> releasedSceneNum(String year);
    Map<String,Integer> releasedPersonNum(String year);
    Map<String,Integer> releasedGoodsNum(String year);
    Map<String,Integer> releasedOthersNum(String year);

    //各种类型每月进行中的项目数
    Map<String,Integer> waitUndertakeAnimalNum(String year);
    Map<String,Integer> waitUndertakeSceneNum(String year);
    Map<String,Integer> waitUndertakePersonNum(String year);
    Map<String,Integer> waitUndertakeGoodsNum(String year);
    Map<String,Integer> waitUndertakeOthersNum(String year);

    //各种类型每月完成的项目数
    Map<String,Integer> finishedAnimalNum(String year);
    Map<String,Integer> finishedSceneNum(String year);
    Map<String,Integer> finishedPersonNum(String year);
    Map<String,Integer> finishedGoodsNum(String year);
    Map<String,Integer> finishedOthersNum(String year);

    //一年来每类项目的平均发布数
    Map<String,Double> avgReleasedNum(String year);

    ResultMessage markCombineRes(String pid,int score);

}
