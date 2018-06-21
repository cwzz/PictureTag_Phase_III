package com.vo.projectvo;

import com.dao.PersonalTagDao;
import com.dao.ProjectDao;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.model.PersonalTag;
import com.model.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Data
public class FinishCondition {

    private int cixu;
    private String uid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "GMT+08:00")
    private Date submitTime;
    private long spendTime;

    public FinishCondition(){}

    public FinishCondition(int cixu,String uid,Date submitTime,long spendTime){
        this.cixu=cixu;
        this.uid=uid;
        this.submitTime=submitTime;
        this.spendTime=spendTime;
    }





}
