package com.bl;

import com.enums.ProjectType;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/6/17 21:09
 */
public class Constant {
    public static int PictureNumPerGroup=3;
    public static int MinimalNumToDivide=0;

    public static double RectanglePerPicture=3.5;
    public static double PricePerRectangle=0.17;

    public static ProjectType[] Types={
            ProjectType.ANIMALTAG,
            ProjectType.SCENETAG,
            ProjectType.PERSONTAG,
            ProjectType.GOODSTAG,
            ProjectType.OTHERSTAG
    };

    public static String[] GongXian={"0-20%",
            "20%-40%",
            "40%-60%",
            "60%-80%",
            "80-100%"
    };

    public static int MinimalContractPeople=10;

    public static double RemainTimeToRemind=0.6;

    public static String[] picRange={
            "<500",
            "500-1000",
            "1000-2000",
            "2000-3000",
            ">3000"
    };

    public static String[] finishRange={
        "<15天",
        "15-30天",
        "30-90天",
        ">90天"
    };

    public static String[] contractNum={
            "<15人",
            "15-50人",
            "50-100人",
            "100-200人",
            ">200人"
    };
}
