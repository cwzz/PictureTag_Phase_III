package com.bl.integratebl;



import com.util.oss.OSSClientUtil;

import com.vo.tag.PencilLineVO;
import com.vo.tag.PictureVO;
import com.vo.tag.PositionVO;
import com.vo.tag.RectangleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
@Service
public class DrawPicture {
    @Autowired
    private OSSClientUtil ossClientUtil;
    @Autowired
    private PictureToPictureBlocks pictureToPictureBlocks;

    //传入picture的数组、项目+分类的名称，返回画过之后的图片的url
    public ArrayList<String> drawPictures(ArrayList<PictureVO> pictures, String project_type_name){
        ArrayList<String> urls=new ArrayList<>();
        //OSSClientUtil ossClientUtil=new OSSClientUtil();
        for(int i=0;i<pictures.size();i++){
            String temp_url=ossClientUtil.uploadFile(changeBufferedImageTOInputStream(drawImage(pictures.get(i))),project_type_name+"/"+i+".jpg");
            urls.add("http://"+temp_url);
        }
        return urls;
    }

    //将BufferedImage格式转换为InputStream格式
    private InputStream changeBufferedImageTOInputStream(BufferedImage image){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return is;
    }

    //画图
    private BufferedImage drawImage(PictureVO picture){
        //1.设置bufferedImage和graphic2d
        PictureToPictureBlocks pictureToPictureBlocks=new PictureToPictureBlocks();
        //1.1先用url得到这张图片的二进制流
        byte[] picdata=pictureToPictureBlocks.getBytesFromURL("http://"+picture.getUrl());
        //1.2再将该图片的二进制流转为BufferedImage
        BufferedImage image=pictureToPictureBlocks.getBufferedImageFromBytes(picdata);

        //2.画方框
        image=useRecDraw(image,picture.getRecTag());

        //3.画铅笔
        image=usePencilDraw(image,picture.getPencilTag());

        //4.加入整体描述信息
        image=addAroundDesc(image,picture.getAroundDesc());
        return image;
    }

    //用方框画
    private BufferedImage useRecDraw(BufferedImage image, RectangleVO[] rectangles){
        //1.创建画笔,设置标注信息属性
        Graphics2D g2d=image.createGraphics();
        Font font = new Font("TimesRoman", Font.BOLD, 15);
        g2d.setFont(font);

        //2.将方框画到图片上
        double origin_pic_width=image.getWidth();
        double origin_pic_height=image.getHeight();
        for(int i=0;i<rectangles.length;i++){
            //设置方框粗细及颜色
            String temp_border_width=rectangles[i].getBorder_width();
            Stroke bs   =   new   BasicStroke((float) Double.parseDouble(temp_border_width.substring(0,temp_border_width.length()-2)));
            g2d.setStroke(bs);
            String temp_border_color=rectangles[i].getBorder_color();
            Color color=new Color(Integer.parseInt(temp_border_color.substring(1),16));
            g2d.setPaint(color);

            //2.1获得rectangle对象
            RectangleVO temp_rect=rectangles[i];
            //2.2先将方框画到图片上
            int width= (int) ((temp_rect.getEndX()-temp_rect.getBeginX())*origin_pic_width);
            int height= (int) ((temp_rect.getEndY()-temp_rect.getBeginY())*origin_pic_height);
            g2d.drawRect((int) (temp_rect.getBeginX()*origin_pic_width), (int) (temp_rect.getBeginY()*origin_pic_height),width,height);
            //2.3在这个框中添加文字信息
            //2.3.1根据有几条标注信息来确定标注信息所放置的位置
            String temp_font_color=rectangles[i].getFont_color();
            temp_font_color=temp_font_color.substring(4,temp_font_color.length()-1);
            String[] rgb=temp_font_color.split(",");
            int r=Integer.parseInt(rgb[0]);
            int g=Integer.parseInt(rgb[1].substring(rgb[1].lastIndexOf(" ")+1));
            int b=Integer.parseInt(rgb[2].substring(rgb[2].lastIndexOf(" ")+1));
            Color color1=new Color(r,g,b);
            g2d.setPaint(color1);
            int number_of_marks=temp_rect.getDescription().length;
            int need_height=number_of_marks*15+(number_of_marks-1)*2;
            int top= (int) (temp_rect.getBeginY()*origin_pic_height+(height-need_height)/2+15);
            int left= (int) (temp_rect.getBeginX()*origin_pic_width);
            for(int j=0;j<temp_rect.getDescription().length;j++){
                String message=temp_rect.getDescription()[j];
                //设置文字的位置
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int stringWidth = fontMetrics.stringWidth(message);
                int stringHeight = fontMetrics.getAscent();
                g2d.drawString(message,left+(width-stringWidth)/2,top);
                top+=(stringHeight+2);
            }
        }
        return image;
    }

    //用铅笔画
    private BufferedImage usePencilDraw(BufferedImage image, PencilLineVO[] pencilLines){
        //1.创建画笔,设置标注信息属性
        Graphics2D g2d=image.createGraphics();
        Font font = new Font("TimesRoman", Font.BOLD, 15);
        g2d.setFont(font);

        //2.将曲线画到图片上
        for(int i=0;i<pencilLines.length;i++){
            //2.1获得pencil对象
            PencilLineVO temp_pencil=pencilLines[i];

            //2.2将铅笔画到图上
            ArrayList<int[]> polygon=changePencilStructureToPolygon(temp_pencil.getPath());
            g2d.drawPolygon(polygon.get(0),polygon.get(1),temp_pencil.getPath().length);

            //2.3在这个框中添加文字信息
            //2.3.1根据有几条标注信息来确定标注信息所放置的位置
            //获取上下左右四点
            //PictureToPictureBlocks pictureToPictureBlocks=new PictureToPictureBlocks();
            double[] points=pictureToPictureBlocks.findBorder(temp_pencil);
            int number_of_marks=temp_pencil.getDescription().length;
            int need_height=number_of_marks*15+(number_of_marks-1)*2;

            int top= (int) (points[0]+(points[1]-points[0]-need_height)/2+15);
            int left= (int) points[2];
            for(int j=0;j<temp_pencil.getDescription().length;j++){
                String message=temp_pencil.getDescription()[j];
                //设置文字的位置
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int stringWidth = fontMetrics.stringWidth(message);
                int stringHeight = fontMetrics.getAscent();
                g2d.drawString(message, (int) (left+(points[3]-points[2]-stringWidth)/2),top);
                top+=(stringHeight+2);
            }

        }
        return image;
    }

    //将pencilline拆分成关于横坐标x,纵坐标y及点个数number的ArrayList返回
    private ArrayList<int[]> changePencilStructureToPolygon(PositionVO[] positions){
        ArrayList<int[]> result=new ArrayList();
        int[] x=new int[positions.length];
        int[] y=new int[positions.length];
        for(int i=0;i<positions.length;i++){
            x[i]= (int) positions[i].getX();
            y[i]=(int) positions[i].getY();
        }

        result.add(x);
        result.add(y);
        return result;
    }

    //在这个照片的最下方加入整体描述信息
    public BufferedImage addAroundDesc(BufferedImage origin_image, String aroundDesc) {
        //设置图片
        BufferedImage image = new BufferedImage(origin_image.getWidth(), origin_image.getHeight()+20, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        main.setColor(Color.white);
        main.fillRect(0, 0, origin_image.getWidth(), origin_image.getHeight());
        Graphics mainPic = image.getGraphics();
        mainPic.drawImage(origin_image, 0, 0, origin_image.getWidth(), origin_image.getHeight(), null);
        mainPic.dispose();

        //设置整体描述
        Graphics2D tip = image.createGraphics();
        // 设置区域颜色
        tip.setColor(Color.black);
        // 填充区域并确定区域大小位置
        tip.fillRect(0, origin_image.getHeight(), origin_image.getWidth(), 20);
        // 设置字体颜色，先设置颜色，再填充内容
        tip.setColor(Color.white);
        // 设置字体
        Font tipFont = new Font("宋体", Font.BOLD, 16);
        tip.setFont(tipFont);
        tip.drawString("整体描述："+aroundDesc, 0, origin_image.getHeight()+16);
        return image;
    }

}
