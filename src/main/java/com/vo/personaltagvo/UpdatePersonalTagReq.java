package com.vo.personaltagvo;

import com.vo.tag.PictureVO;
import lombok.Data;

import java.util.ArrayList;

@Data
public class UpdatePersonalTagReq {
    private String pid;
    private String uid;
    private ArrayList<PictureVO> pictures;

    public UpdatePersonalTagReq(){}

    public UpdatePersonalTagReq(String pid,String uid,ArrayList<PictureVO> pictures){
        this.pid=pid;
        this.uid=uid;
        this.pictures=pictures;
    }
}
