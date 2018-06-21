package com.bl.integratebl;


import com.vo.tag.PencilLineVO;

import com.vo.tag.RectangleVO;
import lombok.Data;

import java.awt.image.BufferedImage;

@Data
public class Block {
    private BufferedImage blockdata;       /*标注的照片块的二进制流*/
    private String[] descriptions;   /*对标注块的描述*/
    private String userid;           /*完成标注块的用户*/
    private boolean isPencil;       /*判断是不是铅笔画转为方框*/
    private PencilLineVO pencil;    /*若是铅笔画则存放铅笔的路径*/
    private RectangleVO rectangle;     /*若不是则存放矩形的对象*/

    public Block(){
    }

    public Block(BufferedImage blockdata, String[] descriptions, String userid, RectangleVO rectangle){
        this.blockdata=blockdata;
        this.descriptions=descriptions;
        this.userid=userid;
        this.isPencil=false;
        this.pencil=null;
        this.rectangle=rectangle;
    }

    public Block(BufferedImage blockdata,String[] descriptions, String userid, PencilLineVO pencil){
        this.blockdata=blockdata;
        this.descriptions=descriptions;
        this.userid=userid;
        this.isPencil=true;
        this.pencil=pencil;
        this.rectangle=null;
    }
}
