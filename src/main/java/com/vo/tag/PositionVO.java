package com.vo.tag;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class PositionVO {
    private double x;
    private double y;

    public PositionVO(){}
    public PositionVO(double _x, double _y){
        this.x=_x;
        this.y=_y;
    }
}
