package com.vo.tag;

import lombok.Data;

@Data
public class PictureVO {
    private String url;
    private PencilLineVO[] pencilTag;
    private RectangleVO[] recTag;
    private String aroundDesc;

    public PictureVO() {
    }
    public PictureVO(String url, PencilLineVO[] pencilLines, RectangleVO[] rectangles, String desc){
        this.url=url;
        this.pencilTag=pencilLines;
        this.recTag=rectangles;
        this.aroundDesc=desc;
    }
}
