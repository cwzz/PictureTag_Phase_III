package com.vo.tag;
import com.enums.ProjectState;
import com.model.PersonalTag;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class PersonalTagVO {

    private long ptid;
    private String pid;
    private String uid;

    private Date startTime;
    private Date submitTime;
    private ProjectState state;

    private ArrayList<PictureVO> pictures;
    private double quality;//贡献率
    private double points;//最终得到的积分
    private int rank;

    public PersonalTagVO(){}

    public PersonalTagVO(PersonalTag personalTag){
//        this.pk=personalTagMultiKeysClass;
        this.ptid=personalTag.getPtid();
        this.pid=personalTag.getPid();
        this.uid=personalTag.getUid();
        this.startTime=personalTag.getStartTime();
        this.submitTime=personalTag.getSubmitTime();
        this.state=personalTag.getState();
        this.quality=personalTag.getQuality();
        this.points=personalTag.getPoints();
        this.rank=personalTag.getRank();
    }



}
