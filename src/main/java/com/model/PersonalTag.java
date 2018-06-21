package com.model;


import com.enums.ProjectState;
import com.model.picture.Picture;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "personal_tag")
@Data
public class PersonalTag{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ptid;
    private String pid;
    private String uid;

    private Date startTime;
    private Date submitTime;
    private ProjectState state;

    @OneToMany(cascade = CascadeType.ALL,orphanRemoval = true)//,orphanRemoval = true)
    private Set<Picture> pictures;

    private String workGroup;
    private double quality;//贡献率
    private double points;//最终得 到的积分
    private int rank;

    public PersonalTag(){}

    public PersonalTag(String pid, String uid, Date startTime, Date submitTime, ProjectState state, Set<Picture> pictures,double quality, double points, int rank){
//        this.pk=personalTagMultiKeysClass;
        this.pid=pid;
        this.uid=uid;
        this.startTime=startTime;
        this.submitTime=submitTime;
        this.state=state;
        this.pictures=pictures;
        this.quality=quality;
        this.points=points;
        this.rank=rank;

    }




}
