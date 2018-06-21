package com.bl.integratebl;


import com.bl.integratebl.picture_compare_algorithm.ComparePicture;
import com.bl.personaltagbl.PersonalTagTrans;
import com.bl.project.ProjectBL;
import com.blservice.ProjectBLService;
import com.enums.CombineType;
import com.enums.ProjectState;
import com.model.PersonalTag;
import com.model.picture.PencilLine;
import com.model.picture.Picture;
import com.model.picture.Position;
import com.model.picture.Rectangle;
import com.vo.personaltagvo.CombineResVO;
import com.vo.personaltagvo.UidAndPoints;
import com.vo.tag.PersonalTagVO;
import com.vo.tag.PictureVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class test {

    @Autowired
    private AnalyseData analyseData;
    @Autowired
    private PictureToPictureBlocks pictureToPictureBlocks;
    @Autowired
    private DrawPicture drawPicture;
    @Autowired
    private ComparePicture comparePicture;

    public static void main(String[] args) {
    test t=new test();
    t.go4();
    }

    public void go1(){
        Set<String> set1=new HashSet<>();
        set1.add("女人");
        Set<String> set2=new HashSet<>();
        set2.add("女性");
        Rectangle r1=new Rectangle(167,40,801,329,set1,"","","","",false);
        Rectangle r2=new Rectangle(344,9,598,645,set2,"","","","",false);
        Rectangle r3=new Rectangle(353,18,611,657,set2,"","","","",false);
        Set<Rectangle> ta1=new HashSet<>();
        ta1.add(r1);
        Set<Rectangle> ta2=new HashSet<>();
        ta2.add(r2);
        Set<Rectangle> ta3=new HashSet<>();
        ta3.add(r3);

        Position position1=new Position(0,0,0);
        Position position2=new Position(50,0,1);
        Position position3=new Position(0,50,2);
        Position position4=new Position(50,50,3);
        Set<Position> positionSet=new HashSet<>();
        positionSet.add(position1);
        positionSet.add(position2);
        positionSet.add(position3);
        positionSet.add(position4);
        Set<String> set=new HashSet<>();
        set.add("无");
        PencilLine p=new PencilLine(positionSet,set,"","","","",false);
        Set<PencilLine> pencilLines=new HashSet<>();
        pencilLines.add(p);

        Picture picture1=new Picture("https://cwzz.oss-cn-beijing.aliyuncs.com/20180531040250-wang/314979.jpg",pencilLines,ta1,"风景",0);
        Picture picture2=new Picture("https://cwzz.oss-cn-beijing.aliyuncs.com/20180531040250-wang/314979.jpg",pencilLines,ta2,"景色",1);
        Picture picture3=new Picture("https://cwzz.oss-cn-beijing.aliyuncs.com/20180531040250-wang/314979.jpg",pencilLines,ta3,"风景",2);
        Set<Picture> p1=new HashSet<>();
        p1.add(picture1);
        Set<Picture> p2=new HashSet<>();
        p2.add(picture2);
        Set<Picture> p3=new HashSet<>();
        p3.add(picture3);
        PersonalTag personalTag1=new PersonalTag("pid","001",new Date(),new Date(),ProjectState.EXAMINE,p1,0,0,0);
        PersonalTag personalTag2=new PersonalTag("pid","002",new Date(),new Date(),ProjectState.EXAMINE,p2,0,0,0);
        PersonalTag personalTag3=new PersonalTag("pid","003",new Date(),new Date(),ProjectState.EXAMINE,p3,0,0,0);

        PersonalTagTrans personalTagTrans=new PersonalTagTrans();

        PersonalTagVO vo1=personalTagTrans.transPoToVo(personalTag1);
        PersonalTagVO vo2=personalTagTrans.transPoToVo(personalTag2);
        PersonalTagVO vo3=personalTagTrans.transPoToVo(personalTag3);

        ArrayList<PersonalTagVO> tags=new ArrayList<>();
        tags.add(vo1);
        tags.add(vo2);
        tags.add(vo3);

        //1.将原有数据结构改为比较时的数据结构
        //PictureToPictureBlocks pictureToPictureBlocks=new PictureToPictureBlocks();
        ArrayList<PictureBlocks> new_project=pictureToPictureBlocks.ChangeProjectDataStructure(tags);

        //2.进行比对并返回结果
        //ProjectBLService projectbl=new ProjectBL();
        Compare compare=new Compare(tags,50);
        ArrayList<PictureVO> pictures=compare.comparePictureBlocks(new_project,tags.size());
        //3.将原来的数据结构转换为图片保存至阿里云，并返回保存的url
        //DrawPicture drawPicture=new DrawPicture();
        ArrayList<String> urls=drawPicture.drawPictures(pictures,"pid"+CombineType.ALL);

        //4.将数据保存到后台
        //4.1将uid和points保存为指定的数据结构
        ArrayList<UidAndPoints> uid_and_points=new ArrayList<>();
        for(int i=0;i<tags.size();i++){
            String uid=tags.get(i).getUid();
            UidAndPoints uidAndPoints=new UidAndPoints(uid,0,compare.getCreditsByUserid(uid));
            uid_and_points.add(uidAndPoints);
        }

        //4.2保存数据
        //CombineResVO vo=new CombineResVO("pid",uid_and_points,CombineType.ALL,urls);

        for(int i=0;i<urls.size();i++){
            System.out.println(urls.get(i));
        }
    }

    public void go2(){
        //ComparePicture comparePicture=new ComparePicture();
        try {
            comparePicture.imageSimilarity(ImageIO.read(new File("E:\\a.png")),ImageIO.read(new File("E:\\d.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void go3(){
        analyseData.integrityProject("20180618095409-baba");
        //20180618095409-baba
        //20180617213841-baba
    }

    public void go4(){
        ArrayList<String> arrayList=new ArrayList<>();
        arrayList.add("wx2.sinaimg.cn/mw690/ac38503ely1fesz8m0ov6j20qo140dix.jpg");
        //AnalyseData data=new AnalyseData(null,null, null);
        analyseData.downloadPictures(arrayList);
    }
}
