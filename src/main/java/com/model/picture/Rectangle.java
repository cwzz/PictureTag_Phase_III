package com.model.picture;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * @Author:zhangping
 * @Description: 矩形类，用于确定图片上标注的矩形位置
 * @CreateData: 2018/3/23 10:40
 */
@Data
@Entity
public class Rectangle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rid;
    private double beginX;
    private double beginY;
    private double endX;
    private double endY;
    private String border_color;//线条颜色
    private String border_width;//线条粗细
    private String font_color;//字体颜色
    private String font_width;//字体大小
    private boolean isBold;//是否加粗
    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private Set<String> description;

    public Rectangle(){}
    public Rectangle(double beginX,double beginY,double endX,double endY,Set<String> description,String lineColor,String lineWidth,String fontColor,String fontWidth,boolean isBold){
        this.beginX=beginX;
        this.beginY=beginY;
        this.endX=endX;
        this.endY=endY;
        this.border_color=lineColor;
        this.border_width=lineWidth;
        this.font_color=fontColor;
        this.font_width=fontWidth;
        this.isBold=isBold;
        this.description=description;
    }
}
