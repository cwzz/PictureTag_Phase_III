package com.blservice;

import com.enums.ResultMessage;
import com.vo.tag.PersonalTagVO;
import com.vo.tag.PictureVO;
import com.vo.uservo.ProBriefInfo;

import java.util.ArrayList;


public interface PersonalTagBLService{
    ResultMessage updatePersonalTag(String pid,String uid, ArrayList<PictureVO> pictures);

    ResultMessage addPersonalTag(String pid,String uid,String[] urls,int groupIndex);

    String[] getNextGroupPicture(String pid,String username);

    ResultMessage submitPersonlTag(String pid,String uid);

    ResultMessage updateQualityAndPoints(String pid,String uid, double quality, double points);

    ArrayList<PersonalTagVO> getAllPersonalTagByPid(String pid);

    PersonalTagVO showPersonalTagBySomeOne(String pid,String uid);

    ResultMessage calRank(String pid);

    ResultMessage delPersonalTag(String pid,String uid);

    //提供给用户查询到的用户承包过的项目
    ArrayList<ProBriefInfo> getContract(String username);
}
