package com.bl.integratebl;


import com.vo.tag.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class PictureToPictureBlocks {
    //将原来以用户为分类的数据结构转为以图片为分类的数据结构
    public ArrayList<PictureBlocks> ChangeProjectDataStructure(ArrayList<PersonalTagVO> alltags){
        //1.按照图进行新的分类,有alltags.get(0).getPictures().size()张图片
        // 1.1创建新的数据结构
        int amount_of_pitures=alltags.get(0).getPictures().size();
        ArrayList<PictureBlocks> integrity_project=new ArrayList<>();
        for(int i=0;i<amount_of_pitures;i++){
            PictureBlocks pictureBlocks=new PictureBlocks(alltags.get(0).getPictures().get(i).getUrl());
            integrity_project.add(pictureBlocks);
        }

        //1.2 将数据加入到新的数据结构中
        //1.2.1将第i个人的数据加入数据结构中
        for(int i=0;i<alltags.size();i++){
            //2.2.2将第i个人的第j张图片的所有信息加入数据结构中
            String userId=alltags.get(i).getUid();
            for(int j=0;j<amount_of_pitures;j++){
                PictureVO origin_picture=alltags.get(i).getPictures().get(j);
                integrity_project=ChangePictureToPictureBlocks(integrity_project,origin_picture,j,userId);
            }
        }
        return integrity_project;
    }

    //将Picture类转换为需要的PictureBlocks类
    public ArrayList<PictureBlocks> ChangePictureToPictureBlocks(ArrayList<PictureBlocks> integrity_project, PictureVO picture, int picNumber, String userid){
        PencilLineVO[] pencilLines=picture.getPencilTag();
        RectangleVO[] rectangles=picture.getRecTag();
        String url="http://"+picture.getUrl();
        String aroundDesc=picture.getAroundDesc();

        //1.先加入图片的整体描述
        String[] temp_string=new String[2];
        temp_string[0]=userid;
        temp_string[1]=aroundDesc;
        integrity_project.get(picNumber).addAroundDesc(temp_string);

        //2.加入block信息
        PictureToPictureBlocks change=new PictureToPictureBlocks();
        //2.1先用url得到这张图片的二进制流
        byte[] picdata=change.getBytesFromURL(url);
        //2.2再将该图片的二进制流转为BufferedImage
        BufferedImage image=change.getBufferedImageFromBytes(picdata);
        //2.3用BufferedImage的方法裁剪图片并将其保存
        double origin_pic_width=image.getWidth();
        double origin_pic_height=image.getHeight();
        //2.3.1若是矩形框
        for(int i=0;i<rectangles.length;i++){
            RectangleVO rec=rectangles[i];
            BufferedImage block_image;
            if((rec.getBeginX()<=1)&&(rec.getBeginY()<=1)&&(rec.getEndY()<=1)&&(rec.getEndY()<=1)){
                 block_image=image.getSubimage((int)(rec.getBeginX()*origin_pic_width),(int)(rec.getBeginY()*origin_pic_height),(int)((rec.getEndX()-rec.getBeginX())*origin_pic_width),(int)((rec.getEndY()-rec.getBeginY())*origin_pic_height));
            }
            else{
                block_image=image.getSubimage((int)rec.getBeginX(),(int)rec.getBeginY(),(int)(rec.getEndX()-rec.getBeginX()),(int)(rec.getEndY()-rec.getBeginY()));
            }
            Block block=new Block(block_image,rec.getDescription(),userid,rec);
            integrity_project.get(picNumber).addBlock(block);
        }
        //2.3.2若是铅笔画
        for(int i=0;i<pencilLines.length;i++){
            PencilLineVO pencil=pencilLines[i];
            double[] border_points=findBorder(pencil);
            BufferedImage block_image=image.getSubimage((int)(border_points[2]*origin_pic_width), (int) (border_points[0]*origin_pic_height), (int) ((border_points[3]-border_points[2])*origin_pic_width), (int) ((border_points[1]-border_points[0])*origin_pic_height));
            Block block=new Block(block_image,pencil.getDescription(),userid,pencil);
            integrity_project.get(picNumber).addBlock(block);
        }

        //3.返回指定的数据结构
        return integrity_project;
    }

    //通过图片的url获取图片二进制流
    public byte[] getBytesFromURL(String picurl) {
        //new一个URL对象
        URL url = null;
        try {
            url = new URL(picurl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        //打开链接
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设置请求方式为"GET"
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = null;
        try {
            inStream = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = new byte[0];
        try {
            data = readInputStream(inStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
//        //new一个文件对象用来保存图片，默认保存当前工程根目录
//        File imageFile = new File("BeautyGirl.jpg");
//        //创建输出流
//        FileOutputStream outStream = new FileOutputStream(imageFile);
//        //写入数据
//        outStream.write(data);
//        //关闭输出流
//        outStream.close();
    }

    //将inputstream转为二进制流
    public byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    //将二进制流转为BufferedImage格式
    public BufferedImage getBufferedImageFromBytes(byte[] data) {
        ByteArrayInputStream in = new ByteArrayInputStream(data);    //将b作为输入流；
        BufferedImage image = null;
        try {
            image = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    //找到铅笔画的最上最下最左最右四点然后画出矩形框，以上下左右的形式返回
    public double[] findBorder(PencilLineVO pencil){
        double[] result=new double[4];
        PositionVO[] path=pencil.getPath();
        PositionVO left=path[0];
        PositionVO right=path[0];
        PositionVO up=path[0];
        PositionVO down=path[0];
        for(int i=0;i<path.length;i++){
            if(path[i].getX()<left.getX()){
                left=path[i];
            }
            if(path[i].getX()>right.getX()){
                right=path[i];
            }
            if(path[i].getY()<up.getY()){
                up=path[i];
            }
            if(path[i].getY()>down.getY()){
                down=path[i];
            }
        }

        result[0]=up.getY();
        result[1]=down.getY();
        result[2]=left.getX();
        result[3]=right.getX();
        return result;
    }
}
