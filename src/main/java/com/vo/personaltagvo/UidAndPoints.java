package com.vo.personaltagvo;

import lombok.Data;

@Data
public class UidAndPoints {
    private String uid;
    double quality;
    private double points;

    public UidAndPoints(){}

    public UidAndPoints(String uid,double quality,double points){
        this.uid=uid;
        this.quality=quality;
        this.points=points;
    }
}
