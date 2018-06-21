package com.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "project_statistics")
public class ProjectStatistics {
    @Id
    private String yearAndMonth;//将年月作为唯一标识符

    private int releasedAnimalNum;//每月新发布的动物类数量
    private int releasedSceneNum;//风景
    private int releasedPersonNum;//人物
    private int releasedGoodsNum;//物品
    private int releasedOthersNum;//其他

    private int waitUndertakeAnimalNum;//每月进行中的动物类数量
    private int waitUndertakeSceneNum;//风景
    private int waitUndertakePersonNum;//人物
    private int waitUndertakeGoodsNum;//物品
    private int waitUndertakeOthersNum;//其他

    private int finishedAnimalNum;//每月完成的动物类数量
    private int finishedSceneNum;//风景
    private int finishedPersonNum;//人物
    private int finishedGoodsNum;//物品
    private int finishedOthersNum;//其他

    private int registerPerMonth;//每月注册人数

    public ProjectStatistics(){}

    public ProjectStatistics(String yearAndMonth){
        this.yearAndMonth=yearAndMonth;

        this.releasedAnimalNum=0;
        this.releasedSceneNum=0;
        this.releasedPersonNum=0;
        this.releasedGoodsNum=0;
        this.releasedOthersNum=0;

        this.waitUndertakeAnimalNum=0;
        this.waitUndertakeSceneNum=0;
        this.waitUndertakePersonNum=0;
        this.waitUndertakeGoodsNum=0;
        this.waitUndertakeOthersNum=0;

        this.finishedAnimalNum=0;
        this.finishedSceneNum=0;
        this.finishedPersonNum=0;
        this.finishedGoodsNum=0;
        this.finishedOthersNum=0;
        this.registerPerMonth=0;
    }

    public ProjectStatistics(String yearAndMonth,int releasedAnimalNum,int releasedSceneNum,int releasedPersonNum,
                             int releasedGoodsNum,int releasedOthersNum,int waitUndertakeAnimalNum,int waitUndertakeSceneNum,
                             int waitUndertakePersonNum,int waitUndertakeGoodsNum,int waitUndertakeOthersNum,
                             int finishedAnimalNum,int finishedSceneNum,int finishedPersonNum,int finishedGoodsNum,int finishedOthersNum){
        this.yearAndMonth=yearAndMonth;

        this.releasedAnimalNum=releasedAnimalNum;
        this.releasedSceneNum=releasedSceneNum;
        this.releasedPersonNum=releasedPersonNum;
        this.releasedGoodsNum=releasedGoodsNum;
        this.releasedOthersNum=releasedOthersNum;

        this.waitUndertakeAnimalNum=waitUndertakeAnimalNum;
        this.waitUndertakeSceneNum=waitUndertakeSceneNum;
        this.waitUndertakePersonNum=waitUndertakePersonNum;
        this.waitUndertakeGoodsNum=waitUndertakeGoodsNum;
        this.waitUndertakeOthersNum=waitUndertakeOthersNum;

        this.finishedAnimalNum=finishedAnimalNum;
        this.finishedSceneNum=finishedSceneNum;
        this.finishedPersonNum=finishedPersonNum;
        this.finishedGoodsNum=finishedGoodsNum;
        this.finishedOthersNum=finishedOthersNum;
    }



}
