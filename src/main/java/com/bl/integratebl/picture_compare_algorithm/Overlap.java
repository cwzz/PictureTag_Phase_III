package com.bl.integratebl.picture_compare_algorithm;

import com.bl.integratebl.Block;
import com.vo.tag.PencilLineVO;
import com.vo.tag.RectangleVO;

public class Overlap {
    public double calculateOverlap(RectangleVO rectangle1, RectangleVO rectangle2){
        double beginX1=rectangle1.getBeginX();
        double beginY1=rectangle1.getBeginY();
        double endX1=rectangle1.getEndX();
        double endY1=rectangle1.getEndY();
        double beginX2=rectangle2.getBeginX();
        double beginY2=rectangle2.getBeginY();
        double endX2=rectangle2.getEndX();
        double endY2=rectangle2.getEndY();

        double square1=(endX1-beginX1)*(endY1*beginY1);
        double square2=(endX2-beginX2)*(endY2-beginY2);

        boolean dot1=isInRectangle(beginX1,beginY1,rectangle2);
        boolean dot2=isInRectangle(endX1,beginY1,rectangle2);
        boolean dot3=isInRectangle(beginX1,endY1,rectangle2);
        boolean dot4=isInRectangle(endX1,endY1,rectangle2);


        if((!dot1)&&(!dot2)&&(!dot3)&&(!dot4)){
            dot1=isInRectangle(beginX2,beginY2,rectangle1);
            dot2=isInRectangle(endX2,beginY2,rectangle1);
            dot3=isInRectangle(beginX2,endY2,rectangle1);
            dot4=isInRectangle(endX2,endY2,rectangle1);
        }

        if(dot1&&dot2&&dot3&&dot4){
            return 100;
        }
        else if(dot1&&dot2){
            double square=(endX1-beginX1)*(endY2-beginY1);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot1&&dot3){
            double square=(endX2-beginX1)*(endY1-beginY1);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot2&&dot4){
            double square=(endX1-beginX2)*(endY1-beginY1);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot3&&dot4){
            double square=(endX1-beginX1)*(endY1-beginY2);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot1){
            double square=(endX2-beginX1)*(endY2-beginY1);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot2){
            double square=(endX1-beginX2)*(endY2-beginY1);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot3){
            double square=(endX2-beginX1)*(endY1-beginY2);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else if(dot4){
            double square=(endX1-beginX2)*(endY1-beginY2);
            if(square1>square2){
                return square/square2*100;
            }
            else{
                return square/square1*100;
            }
        }
        else{
            return 0;
        }
    }

    private boolean isInRectangle(double x, double y, RectangleVO rectangleVO){
        if(x>=rectangleVO.getBeginX()){
            if(x<=rectangleVO.getEndX()){
                if(y>=rectangleVO.getBeginY()){
                    if(y<=rectangleVO.getEndY()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
