package com.model;


import com.enums.ProjectState;
import com.model.picture.Picture;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
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

    @ElementCollection(targetClass = String.class,fetch = FetchType.LAZY)
    private List<String> resulturl;//当工人提交工作结果后，把他的标注信息转为url并保存

    private String workGroup;
    private double quality;//贡献率
    private double points;//最终得 到的积分
    private int rank;

    public PersonalTag(){}

    public PersonalTag(String pid, String uid, Date startTime, Date submitTime, ProjectState state, Set<Picture> pictures,double quality, double points, int rank){
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
