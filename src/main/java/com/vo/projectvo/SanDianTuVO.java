package com.vo.projectvo;

import lombok.Data;

@Data
public class SanDianTuVO {
    private int zhibiao1;
    private int zhibiao2;
    private double zhibiao3;

    public SanDianTuVO(){}

    public SanDianTuVO(int zhibiao1,int zhibiao2,double zhibiao3){
        this.zhibiao1=zhibiao1;
        this.zhibiao2=zhibiao2;
        this.zhibiao3=zhibiao3;
    }
}
