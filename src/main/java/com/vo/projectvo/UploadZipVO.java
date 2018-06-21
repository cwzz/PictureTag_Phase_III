package com.vo.projectvo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/6/16 10:24
 */
@Data
public class UploadZipVO {

    private String projectID;
    private MultipartFile zipFile;

    public UploadZipVO(){}
    public UploadZipVO(MultipartFile zipFile, String projectID){
        this.projectID=projectID;
        this.zipFile=zipFile;
    }
}
