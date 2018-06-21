package com.vo.personaltagvo;

import lombok.Data;

@Data
public class ShowReq {
    private String pid;
    private String uid;

    public ShowReq(){}

    public ShowReq(String pid,String uid){
        this.pid=pid;
        this.uid=uid;
    }
}
