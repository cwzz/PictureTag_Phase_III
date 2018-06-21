package com.model.picture;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "position")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long tid;
    private double x;
    private double y;
    private int shunxu;

    public Position(){}
    public Position(double _x,double _y,int shunxu){
        this.x=_x;
        this.y=_y;
        this.shunxu=shunxu;
    }
}
