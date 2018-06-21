package com.model.picture;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "pictures")
public class Picture{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long urid;

    private String url;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<PencilLine> pencilTag;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<Rectangle> recTag;

    private String aroundDesc;

    private int shunxu;

    public Picture() {
    }
    public Picture(String url, Set<PencilLine> pencilLines, Set<Rectangle> rectangles, String desc,int shunxu){
        this.url=url;
        this.pencilTag=pencilLines;
        this.recTag=rectangles;
        this.aroundDesc=desc;
        this.shunxu=shunxu;
    }

}
