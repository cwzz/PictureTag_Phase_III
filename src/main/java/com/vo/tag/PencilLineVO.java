package com.vo.tag;

import lombok.Data;

/**
 * @Author:zhangping
 * @Description: 线条类
 * @CreateData: 2018/3/23 11:28
 */

@Data
public class PencilLineVO {
    private PositionVO[] path;
    private String[] description;
    private String border_color;//线条颜色
    private String border_width;//线条粗细
    private String font_color;//字体颜色
    private String font_width;//字体大小
    private boolean isBold;//是否加粗

    public PencilLineVO(){}
    public PencilLineVO(PositionVO[] _path, String[] description,String lineColor,String lineWidth,String fontColor,String fontWidth,boolean isBold){
        this.path=_path;
        this.description=description;
        this.border_color=lineColor;
        this.border_width=lineWidth;
        this.font_color=fontColor;
        this.font_width=fontWidth;
        this.isBold=isBold;
    }


}
