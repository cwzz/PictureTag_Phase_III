package com.vo.personaltagvo;


import com.enums.CombineType;

import java.util.ArrayList;

public class TypeAndUrl {
    private ArrayList<String> url;
    private CombineType combineType;

    public TypeAndUrl(){}

    public TypeAndUrl(ArrayList<String> url,CombineType combineType){
        this.url=url;
        this.combineType=combineType;
    }
}
