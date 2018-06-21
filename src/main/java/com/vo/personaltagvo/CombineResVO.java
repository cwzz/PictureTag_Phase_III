package com.vo.personaltagvo;

import com.enums.CombineType;
import lombok.Data;

import java.util.ArrayList;

@Data
public class CombineResVO {
    private String pid;
    private ArrayList<UidAndPoints> uidAndPoints;
    private ArrayList<String> url;

    public CombineResVO(){}

    public CombineResVO(String pid,ArrayList<UidAndPoints> uidAndPoints,ArrayList<String> url){
        this.pid=pid;
        this.uidAndPoints=uidAndPoints;
        this.url=url;
    }
}
