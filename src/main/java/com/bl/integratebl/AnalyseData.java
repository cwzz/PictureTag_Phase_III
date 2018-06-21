package com.bl.integratebl;


import com.blservice.PersonalTagBLService;
import com.blservice.ProjectBLService;
import com.enums.CombineType;
import com.enums.ResultMessage;
import com.vo.personaltagvo.CombineResVO;
import com.vo.personaltagvo.UidAndPoints;
import com.vo.tag.PersonalTagVO;
import com.vo.tag.PictureVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.xml.crypto.Data;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AnalyseData {

    @Autowired
    private PersonalTagBLService personalTagBLService;
    @Autowired
    private ProjectBLService projectBLService;
    @Autowired
    private PictureToPictureBlocks pictureToPictureBlocks;
    @Autowired
    private DrawPicture drawPicture;

    //对项目进行整合，并将结果保存至后台
    public void integrityProject(String projectid){
        //1.根据projectid拿到所有用户对于该项目进行的标注对象personalTag
        ArrayList<PersonalTagVO> all_tags=personalTagBLService.getAllPersonalTagByPid(projectid);

        //2.获得相应分类的整合信息并把数据存入后台
        integrityProjectByType(all_tags, projectid,CombineType.ALL);
    }

    //将分好类的数据结构进行系统自动整合，并将整合的结果保存到后台
    private void integrityProjectByType(ArrayList<PersonalTagVO> tags, String projectid, CombineType combineType){
        //1.将原有数据结构改为比较时的数据结构
        //PictureToPictureBlocks pictureToPictureBlocks=new PictureToPictureBlocks();
        ArrayList<PictureBlocks> new_project=pictureToPictureBlocks.ChangeProjectDataStructure(tags);
        System.out.println("finish changing data structure");

        //2.进行比对并返回结果
        Compare compare=new Compare(tags,projectBLService.viewPro(projectid).getPoints());
        ArrayList<PictureVO> pictures=compare.comparePictureBlocks(new_project,tags.size());
        System.out.println("finish comparing pictures");

        //3.将原来的数据结构转换为图片保存至阿里云，并返回保存的url
        //DrawPicture drawPicture=new DrawPicture();
        ArrayList<String> urls=drawPicture.drawPictures(pictures,projectid+combineType);
        System.out.println("finish saving pictures in ALI");

        //4.将数据保存到后台
        //4.1将uid和points保存为指定的数据结构
        ArrayList<UidAndPoints> uid_and_points=new ArrayList<>();
        for(int i=0;i<tags.size();i++){
            String uid=tags.get(i).getUid();
            UidAndPoints uidAndPoints=new UidAndPoints(uid,0,compare.getCreditsByUserid(uid));
            uid_and_points.add(uidAndPoints);
        }


        //4.2保存数据
        CombineResVO vo=new CombineResVO(projectid,uid_and_points,urls);
//        CombineResBLService combineResBLService=new CombineResBL();
//        combineResBLService.saveCombineRes(vo);
        projectBLService.saveCombineRes(vo);
        System.out.println("finish saving integrity result in ALI");
    }

    //根据整合的url下载图片到本地
    public String downloadPictures(ArrayList<String> urls){
//        //1.弹出对话框获取存储路径
//        //美化
//        JFileChooser jfc = new JFileChooser();
//        try {
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//        SwingUtilities.updateComponentTreeUI(jfc);
//
//        //设置当前路径为桌面路径,否则将我的文档作为默认路径
//        FileSystemView fsv = FileSystemView .getFileSystemView();
//        jfc.setCurrentDirectory(fsv.getHomeDirectory());
//
//        //JFileChooser.FILES_AND_DIRECTORIES 选择路径和文件
//        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
//
//        //弹出的提示框的标题
//        jfc.showDialog(new JLabel(), "确定");
//       //用户选择的路径或文件
//        File file=jfc.getSelectedFile();
//        String path=file.getPath();
        FileSystemView fsv=FileSystemView.getFileSystemView();
        String path=fsv.getHomeDirectory().getPath();
        SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");
        File file=new File(path+File.separator+"整合结果"+df.format(new Date()));
        file.mkdirs();
        path=file.getPath();

        //2.将图片根据url写到相应内存中
        //PictureToPictureBlocks pictureToPictureBlocks=new PictureToPictureBlocks();
        for(int i=0;i<urls.size();i++){
            //2.1现将url转为BufferedImage对象
            BufferedImage image=pictureToPictureBlocks.getBufferedImageFromBytes(pictureToPictureBlocks.getBytesFromURL(urls.get(i)));
            String type=urls.get(i).substring(urls.get(i).lastIndexOf('.')+1);
            try {
                ImageIO.write(image,type,new File(path+File.separator+(i+1)+"."+type));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    //估算整合所需要的时间,返回所需分钟数
    public double predictTime(String project_id){
        //一个框的转换时间
        double change_time=0;
        //两个框的比较时间
        double comapre_time=0;
        //存储一张图的时间
        double savepic_time=0;
        //存储一张图项目的时间
        double savepro_time=0;

        return 0;
    }

}
