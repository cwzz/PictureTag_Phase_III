package com.vo.uservo;

import lombok.Data;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/6/22 10:31
 */
@Data
public class SanDianTuUser {
    private double zhibiao1;//积分
    private double zhibiao2;//项目时长，几天,可以是小数
    private int zhibiao3;//承包人数

    public SanDianTuUser(){}

    public SanDianTuUser(double zhibiao1, double zhibiao2, int zhibiao3){
        this.zhibiao1=zhibiao1;
        this.zhibiao2=zhibiao2;
        this.zhibiao3=zhibiao3;
    }
}
