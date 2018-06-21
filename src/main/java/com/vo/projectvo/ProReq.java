package com.vo.projectvo;

import lombok.Data;

@Data
public class ProReq {
    private String username;
    private String pid;

    public ProReq(){}

    public ProReq(String username,String pid){
        this.username=username;
        this.pid=pid;
    }
}
