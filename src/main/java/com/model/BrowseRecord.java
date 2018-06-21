package com.model;

import com.enums.ProjectType;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "record")
public class BrowseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reid;

    private String projectID;
    private Date browseTime;//最后一次浏览时间
    private ProjectType type;
    private int times;//用户浏览这个项目的次数

    public BrowseRecord(){}
}
