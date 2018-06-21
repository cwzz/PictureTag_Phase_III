package com.util.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class OSSClientUtil{

    private ZipInputStream  zipIn;      //解压Zip
    private ZipEntry zipEntry;
    private byte[] buf;
    private int readedBytes;

    private static String ENDPOINT;
    private static String AccessKeyId;
    private static String AccessKeySecret;
    private static String BucketName;
    private static String FileSeparator;
    private static String localSeparator;
    private static String tempFile;

    private OSSClient ossClient;

    static {
        ENDPOINT= OSSClassConstant.endpoint;
        AccessKeyId=OSSClassConstant.accessKeyId;
        AccessKeySecret=OSSClassConstant.accessKeySecret;
        BucketName=OSSClassConstant.bucketName;
        FileSeparator="/";
        localSeparator=File.separator;
        tempFile="temp";
    }

    public OSSClientUtil(){
        this.init();
    }

    /*
     * 初始化
     */
    public void init(){
        ossClient=new OSSClient(ENDPOINT,AccessKeyId,AccessKeySecret);
        this.buf=new byte[512];
    }

    /**
     * 销毁
     */
    public void destroy(){
        ossClient.shutdown();
    }

    public String uploadPicture(String projectID,String filename,String base64){
        //截取出图片类型
        int end=base64.indexOf(";base64");
        String type=base64.substring(11,end);
        base64=base64.substring(end+8);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] bytes1 = decoder.decode(base64);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes1);
        int sepatator=filename.lastIndexOf(".");
        return this.uploadFile(inputStream,projectID+FileSeparator+filename.substring(0,sepatator)+"."+type);
    }

    public ArrayList<String> uploadZip(MultipartFile zipFile,String projectID){
        ArrayList<String> result=new ArrayList<>();

        FileOutputStream fileOut;
        FileInputStream fileInputStream;
        File file;

        try{

            File tempSpace=new File(tempFile);
            if(!tempSpace.exists()){
                tempSpace.mkdirs();
            }
            this.zipIn = new ZipInputStream(new BufferedInputStream(zipFile.getInputStream()));

            while((this.zipEntry = this.zipIn.getNextEntry()) != null){
                file = new File(tempFile+localSeparator+this.zipEntry.getName());

                if(this.zipEntry.isDirectory()){
                    file.mkdirs();
                }
                else{
                    fileOut = new FileOutputStream(file);
                    while(( this.readedBytes = this.zipIn.read(this.buf) ) > 0){
                        fileOut.write(this.buf , 0 , this.readedBytes );
                    }
                    fileOut.close();
                    fileInputStream = new FileInputStream(file);
                    result.add(this.uploadFile(fileInputStream,projectID+FileSeparator+file.getName()));
                }
                this.zipIn.closeEntry();
            }
            deleteAllFile(tempFile);
            return result;
        }catch(IOException ioe){
            ioe.printStackTrace();
            return null;
        }
    }

    private boolean deleteAllFile(String path){
        boolean flag=true;
        File file = new File(path);
        if (!file.exists()) {
            flag= false;
        }
        if (!file.isDirectory()) {
            flag= false;
        }
        File temp = null;
        String[] list=file.list();
        if(list!=null){
            for (String aTempList :list) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + aTempList);
                } else {
                    temp = new File(path + File.separator + aTempList);
                }
                if (temp.isFile()) {
                    flag=temp.delete();
                }
                if (temp.isDirectory()) {
                    deleteAllFile(path + File.separator + aTempList);//先删除文件夹里面的文件
                    flag=delFolder(path + File.separator + aTempList);//再删除空文件夹
                }
            }
        }
        return flag;
    }
    private static boolean delFolder(String folderPath) {
        try {
            File myFilePath = new File(folderPath);
            return myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传到OSS服务器  如果同名文件会覆盖服务器上的
     * @param inputStream 文件流
     * @param fileName 文件名称 包括后缀名
     * @return 出错返回"" ,唯一MD5数字签名
     */
    public String uploadFile(InputStream inputStream, String fileName) {
        String ret = "";
        String url="";
        try {
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(getContentType(fileName.substring(fileName.lastIndexOf("."))));
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            //上传文件
            PutObjectResult putResult = ossClient.putObject(BucketName,fileName, inputStream, objectMetadata);
            ret = putResult.getETag();
            url = BucketName+"."+ENDPOINT+"/"+fileName;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(ret.equals("")){
            return "上传图片失败";
        }else{
            return url;
        }
    }

    /**
     * Description: 判断OSS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return String
     */
    private static String getContentType(String FilenameExtension) {
        if (FilenameExtension.equalsIgnoreCase(".bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase(".gif")) {
            return "image/gif";
        }
        if (FilenameExtension.equalsIgnoreCase(".jpeg") ||
                FilenameExtension.equalsIgnoreCase(".jpg") ||
                FilenameExtension.equalsIgnoreCase(".png")) {
            return "image/jpeg";
        }
        if (FilenameExtension.equalsIgnoreCase(".html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase(".txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase(".vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase(".pptx") ||
                FilenameExtension.equalsIgnoreCase(".ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase(".docx") ||
                FilenameExtension.equalsIgnoreCase(".doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase(".xml")) {
            return "text/xml";
        }
        return "image/jpeg";
    }
}
