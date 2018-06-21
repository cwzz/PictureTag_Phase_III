package com.vo.personaltagvo;

import com.enums.CombineType;
import com.enums.ProjectType;
import lombok.Data;

@Data
public class ShowCombineResReq {
    private String pid;
    private CombineType combineType;

    public ShowCombineResReq(){}

    public ShowCombineResReq(String pid,CombineType combineType){
        this.pid=pid;
        this.combineType=combineType;
    }
}
