package com.bl.personaltagbl;

import com.bl.Constant;
import com.model.PersonalTag;
import com.model.picture.PencilLine;
import com.model.picture.Picture;
import com.model.picture.Position;
import com.model.picture.Rectangle;
import com.util.TransSetToArray;
import com.vo.tag.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PersonalTagTrans {

    @Autowired
    private TransSetToArray transSetToArray;

    public PersonalTagVO transPoToVo(PersonalTag personalTag){
        PersonalTagVO personalTagVO=new PersonalTagVO(personalTag);
        //po中的picturePO转为PictureVO
        Set<Picture> picturesPO=personalTag.getPictures();
        personalTagVO.setPictures(this.transPictureToVo(picturesPO));
        return personalTagVO;
    }
    public PersonalTagVO transPoToVoPart(PersonalTag personalTag){
        PersonalTagVO personalTagVO=new PersonalTagVO(personalTag);
        String[] all=personalTag.getWorkGroup().split(" ");
        int begin=(all.length-1)* Constant.PictureNumPerGroup;
        int end=personalTag.getPictures().size();
        //po中的picturePO转为PictureVO
        Set<Picture> picturesPO=personalTag.getPictures();
        personalTagVO.setPictures(this.transPictureToVo(picturesPO,begin,end));
        return personalTagVO;
    }

    private ArrayList<PictureVO> transPictureToVo(Set<Picture> pictures,int begin,int end){
        PictureVO[] pictureVOS=new PictureVO[end-begin];
        for(Picture picture:pictures){
            int order=picture.getShunxu();
            if(order>=begin){
                PencilLineVO[] pencilLineVOS=this.transPencilToVo(picture.getPencilTag());
                RectangleVO[] rectangleVOS=this.transRectangleToVo(picture.getRecTag());
                pictureVOS[order-begin]=new PictureVO(picture.getUrl(),pencilLineVOS,rectangleVOS,picture.getAroundDesc());
            }
        }
        return new ArrayList<>(Arrays.asList(pictureVOS));
    }
    public ArrayList<PictureVO> transPictureToVo(Set<Picture> pictures){
        PictureVO[] pictureVOS=new PictureVO[pictures.size()];
        for (Picture picture:pictures){
            PencilLineVO[] pencilLineVOS=this.transPencilToVo(picture.getPencilTag());
            RectangleVO[] rectangleVOS=this.transRectangleToVo(picture.getRecTag());
            //result.add(new PictureVO(picture.getUrl(),pencilLineVOS,rectangleVOS,picture.getAroundDesc()));
            pictureVOS[picture.getShunxu()]=new PictureVO(picture.getUrl(),pencilLineVOS,rectangleVOS,picture.getAroundDesc());
        }
        return new ArrayList<>(Arrays.asList(pictureVOS));
    }

    public PencilLineVO[] transPencilToVo(Set<PencilLine> pencilLines){
        if(pencilLines==null){
            return null;
        }
        PencilLineVO[] results=new PencilLineVO[pencilLines.size()];
        int index=0;
        for(PencilLine pencilLine:pencilLines){
            PositionVO[] positions=new PositionVO[pencilLine.getPath().size()];
            for(Position position:pencilLine.getPath()){
                positions[position.getShunxu()]=new PositionVO(position.getX(),position.getY());
            }
            results[index]=new PencilLineVO(positions,transSetToArray.transSetToStringArray(pencilLine.getDescription()),pencilLine.getBorder_color(),pencilLine.getBorder_width(),pencilLine.getFont_color(),pencilLine.getFont_width(),pencilLine.isBold());
            index++;
        }
        return results;
    }

    private RectangleVO[] transRectangleToVo(Set<Rectangle> rectangles){
        if (rectangles==null){
            return null;
        }
        RectangleVO[] rectangleVOS=new RectangleVO[rectangles.size()];
        int index=0;
        for(Rectangle rectangle:rectangles){
            rectangleVOS[index]=new RectangleVO(rectangle.getBeginX(),rectangle.getBeginY(),rectangle.getEndX(),rectangle.getEndY(),transSetToArray.transSetToStringArray(rectangle.getDescription()),rectangle.getBorder_color(),rectangle.getBorder_width(),rectangle.getFont_color(),rectangle.getFont_width(),rectangle.isBold());
            index++;
        }
        return rectangleVOS;
    }

    Set<Picture> transPictureToPo(ArrayList<PictureVO> pictureVOS){
        Set<Picture> result=new HashSet<>();
        for(int i=0;i<pictureVOS.size();i++){

            Picture picture=new Picture(pictureVOS.get(i).getUrl(),this.transPencilToPo(pictureVOS.get(i).getPencilTag()),this.transRectangleToPo(pictureVOS.get(i).getRecTag()),pictureVOS.get(i).getAroundDesc(),i);
            result.add(picture);
        }
        return result;
    }

    ArrayList<Picture> transPictureToPo2(ArrayList<PictureVO> pictureVOS,int begin){
        ArrayList<Picture> result=new ArrayList<>();
        for(int i=0;i<pictureVOS.size();i++){
            Picture picture=new Picture(pictureVOS.get(i).getUrl(),this.transPencilToPo(pictureVOS.get(i).getPencilTag()),this.transRectangleToPo(pictureVOS.get(i).getRecTag()),pictureVOS.get(i).getAroundDesc(),i+begin);
            result.add(picture);
        }
        return result;
    }

    private Set<Rectangle> transRectangleToPo(RectangleVO[] rectangles){
        Set<Rectangle> result=new HashSet<>();
        for(RectangleVO rectangleVO:rectangles){
            result.add(new Rectangle(rectangleVO.getBeginX(),rectangleVO.getBeginY(),rectangleVO.getEndX(),rectangleVO.getEndY(),new HashSet<>(Arrays.asList(rectangleVO.getDescription())),rectangleVO.getBorder_color(),rectangleVO.getBorder_width(),rectangleVO.getFont_color(),rectangleVO.getFont_width(),rectangleVO.isBold()));
        }
        return result;
    }

    private Set<PencilLine> transPencilToPo(PencilLineVO[] pencilLineVOS){
        Set<PencilLine> result=new HashSet<>();
        Set<Position> paths=new HashSet<>();
        for(PencilLineVO VO:pencilLineVOS){
            result.add(new PencilLine(this.transPositionToPo(VO.getPath()), new HashSet<>(Arrays.asList(VO.getDescription())),VO.getBorder_color(),VO.getBorder_width(),VO.getFont_color(),VO.getFont_width(),VO.isBold()));
        }
        return result;
    }

    private Set<Position> transPositionToPo(PositionVO[] positionVOS){
        Set<Position> positions=new HashSet<>();
        for(int i=0;i<positionVOS.length;i++){
            positions.add(new Position(positionVOS[i].getX(),positionVOS[i].getY(),i));
        }
        return positions;
    }
}
