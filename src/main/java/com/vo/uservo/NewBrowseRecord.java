package com.vo.uservo;

import com.enums.ProjectType;
import lombok.Data;

@Data
public class NewBrowseRecord {
    private String username;
    private String projectID;
    private ProjectType type;

    public NewBrowseRecord(){}
}
