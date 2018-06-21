package com.model.picture;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

/**
 * @Author:zhangping
 * @Description: 线条类
 * @CreateData: 2018/3/23 11:28
 */

@Data
@Entity
public class PencilLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long iid;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Position> path;

    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private Set<String> description;
    private String border_color;//线条颜色
    private String border_width;//线条粗细
    private String font_color;//字体颜色
    private String font_width;//字体大小
    private boolean isBold;//是否加粗

    public PencilLine(){}
    public PencilLine(Set<Position> path, Set<String> description,String lineColor,String lineWidth,String fontColor,String fontWidth,boolean isBold){
        this.path=path;
        this.description=description;
        this.border_color=lineColor;
        this.border_width=lineWidth;
        this.font_color=fontColor;
        this.font_width=fontWidth;
        this.isBold=isBold;
    }


}
