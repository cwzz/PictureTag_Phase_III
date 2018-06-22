package com.vo.projectvo;

import com.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/6/18 22:01
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
//    private int totalFinishedNum;
//    private int totalReleasedNum;

    //每月新发布项目数折线图,进行中(包括评估)项目总数，完成项目总数
    private Map<String,Integer> releasedPerMonth;
    private Map<String,Integer> waitUndertakePerMonth;
    private Map<String,Integer> finishedPerMonth;

    //各种类型每月新发布项目数
    private Map<String,Integer> releasedAnimalNum;
    private Map<String,Integer> releasedSceneNum;
    private Map<String,Integer> releasedPersonNum;
    private Map<String,Integer> releasedGoodsNum;
    private Map<String,Integer> releasedOthersNum;

    //各种类型每月进行中的项目数
    private Map<String,Integer> waitUndertakeAnimalNum;
    private Map<String,Integer> waitUndertakeSceneNum;
    private Map<String,Integer> waitUndertakePersonNum;
    private Map<String,Integer> waitUndertakeGoodsNum;
    private Map<String,Integer> waitUndertakeOthersNum;

    //各种类型每月完成的项目数
    private Map<String,Integer> finishedAnimalNum;
    private Map<String,Integer> finishedSceneNum;
    private Map<String,Integer> finishedPersonNum;
    private Map<String,Integer> finishedGoodsNum;
    private Map<String,Integer> finishedOthersNum;

    //一年来每类项目的平均发布数
        private Map<String,String> avgReleasedNum;

}
