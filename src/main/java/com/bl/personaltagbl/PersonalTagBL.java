package com.bl.personaltagbl;


import com.bl.Constant;
import com.blservice.PersonalTagBLService;
import com.blservice.ProjectBLService;
import com.blservice.UserBLService;
import com.dao.PersonalTagDao;
import com.enums.ProjectState;
import com.enums.ResultMessage;
import com.model.PersonalTag;
import com.model.Project;
import com.model.picture.Picture;
import com.vo.tag.PersonalTagVO;
import com.vo.tag.PictureVO;
import com.vo.uservo.ProBriefInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class PersonalTagBL implements PersonalTagBLService {

    @Autowired
    private PersonalTagDao personalTagDao;
    @Autowired
    private PersonalTagTrans personalTagTrans;
    @Autowired
    private ProjectBLService projectBLService;
    @Autowired
    private UserBLService userBLService;

    @Override
    public ResultMessage updatePersonalTag(String pid,String uid, ArrayList<PictureVO> pictures) {
        for(PictureVO v:pictures){
            System.err.println(v.toString());
        }

        PersonalTag personalTag=personalTagDao.searchByPidAndUid(pid,uid);
        if(personalTag==null){
            return ResultMessage.NOTEXIST;
        }
        int workGroup=personalTag.getWorkGroup().split(" ").length;
        System.err.println(workGroup);
        int begin=(workGroup-1)*Constant.PictureNumPerGroup;
        ArrayList<Picture> updatePictures=personalTagTrans.transPictureToPo2(pictures,begin);
        Set<Picture> pictureSet=personalTag.getPictures();
        Set<Picture> tempSet=new HashSet<>();
        System.err.println("begin:-----------------------"+begin);
        System.err.println(pictureSet.size());
        //pictureSet.clear();
        int i=0;
        for(Picture picture:pictureSet){
            //System.err.println("PICTURESET:----------"+picture.toString());
            if(picture.getShunxu()>=begin){//本次修改的这一组
                tempSet.add(updatePictures.get(i));
                i++;
            }else{//之前的组别
                //picture.setUrid(0);
                tempSet.add(picture);
            }
        }
        pictureSet.clear();
        pictureSet.addAll(tempSet);
//        for(Picture picture:pictureSet){
//            System.err.println("---"+picture.toString());
//        }
//        pictureSet.clear();
//        int i=0;
//        for(Picture picture:pictureSet){
//            if(picture.getShunxu()>=begin){//是要更新的图片
//                pictureSet.remove(picture);
//                pictureSet.add(updatePictures.get(i));
//                i++;
//            }
//        }
        //pictureSet.addAll(personalTagTrans.transPictureToPo(pictures));
        //personalTag.setPictures(pictureSet);
//        System.err.println(personalTag.toString());
        personalTagDao.save(personalTag);
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage addPersonalTag(String pid,String uid,String[] urls,int groupIndex) {
        try{
            PersonalTag personalTag=new PersonalTag();
            personalTag.setPid(pid);
            personalTag.setUid(uid);
            personalTag.setStartTime(new Date());
            personalTag.setState(ProjectState.TAGING);
            personalTag.setWorkGroup(String.valueOf(groupIndex));
            Set<Picture> pictures=new HashSet<>();
            for(int i=0;i<urls.length;i++){
                Picture p=new Picture();
                p.setUrl(urls[i]);
                p.setShunxu(i);
                pictures.add(p);
            }
            personalTag.setPictures(pictures);
            personalTagDao.saveAndFlush(personalTag);
            return ResultMessage.SUCCESS;
        }catch (Exception e){
            return ResultMessage.FAIL;
        }
    }

    @Override
    public String[] getNextGroupPicture(String projectID, String username) {
        PersonalTag personalTag=personalTagDao.searchByPidAndUid(projectID,username);
        int nextIndex=projectBLService.getNextGroupIndex(projectID,personalTag.getWorkGroup());
        if(nextIndex==-1){//该用户已经做完该项目的全部图片
            return null;
        }else{
            Set<Picture> pictures=personalTag.getPictures();
            String[] newGroup=projectBLService.getNextGroupUrl(projectID,nextIndex);
            int groupNum=personalTag.getWorkGroup().split(" ").length;
            for(int i=0;i<newGroup.length;i++){
                Picture p=new Picture();
                p.setUrl(newGroup[i]);
                p.setShunxu(i+(groupNum* Constant.PictureNumPerGroup));
                pictures.add(p);
            }
            personalTag.setPictures(pictures);
            personalTag.setWorkGroup(personalTag.getWorkGroup()+" "+nextIndex);
            personalTagDao.saveAndFlush(personalTag);
            return newGroup;
        }
    }

    @Override
    public ResultMessage submitPersonlTag(String pid,String uid) {
        try{
            Optional<PersonalTag> t = Optional.ofNullable(personalTagDao.searchByPidAndUid(pid, uid));
            if(!t.isPresent()){
                return ResultMessage.NOTEXIST;
            }else{
                PersonalTag personalTag=t.get();
                personalTag.setSubmitTime(new Date());
                personalTag.setState(ProjectState.SUBMITTED);
                personalTagDao.saveAndFlush(personalTag);
                return ResultMessage.SUCCESS;
            }
        }catch (Exception e){
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ResultMessage updateQualityAndPoints(String pid,String uid, double quality, double points) {
        try{
            Optional<PersonalTag> t = Optional.ofNullable(personalTagDao.searchByPidAndUid(pid, uid));
            if(!t.isPresent()){
                return ResultMessage.NOTEXIST;
            }else{
                personalTagDao.dropByPidAndUid(pid,uid);
                PersonalTag personalTag=t.get();
                personalTag.setState(ProjectState.FINISHED);
                personalTag.setQuality(quality);
                personalTag.setPoints(points);
                //这里有问题，ptid不能设定
                personalTagDao.saveAndFlush(personalTag);
                userBLService.updateCredits(uid,projectBLService.viewPro(pid).getPro_type(),points);
                return ResultMessage.SUCCESS;
            }
        }catch (Exception e){
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ArrayList<PersonalTagVO> getAllPersonalTagByPid(String pid) {
        System.err.println(pid);
        ArrayList<PersonalTag> personalTags=personalTagDao.searchByPid(pid);
        ArrayList<PersonalTagVO> results=new ArrayList<>();
        for(PersonalTag p:personalTags){
            results.add(personalTagTrans.transPoToVo(p));
        }
        return results;
    }

    @Override
    public PersonalTagVO showPersonalTagBySomeOne(String pid, String uid) {
        PersonalTag personalTag=personalTagDao.searchByPidAndUid(pid,uid);
        if(personalTag==null){
            return null;
        }else{
            return personalTagTrans.transPoToVoPart(personalTag);
        }
    }

    @Override
    public ResultMessage calRank(String pid) {
        ArrayList<PersonalTag> personalTags=personalTagDao.searchByPid(pid);
        personalTags.sort((o1, o2) -> Double.compare(o1.getQuality(), o2.getQuality()));
        for(PersonalTag p:personalTags){
            p.setRank(personalTags.indexOf(p)+1);
        }
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage delPersonalTag(String pid, String uid) {
        personalTagDao.dropByPidAndUid(pid,uid);
        return ResultMessage.SUCCESS;
    }

    @Override
    public ArrayList<ProBriefInfo> getContract(String username) {
        ArrayList<ProBriefInfo> result=new ArrayList<>();
        for(PersonalTag personalTag:personalTagDao.searchByUid(username)){
            ProBriefInfo proBriefInfo =projectBLService.getBriefInfo(personalTag.getPid());
            proBriefInfo.setState(personalTag.getState());
            proBriefInfo.setFinalGet(personalTag.getPoints());
            result.add(proBriefInfo);
        }
        return result;
    }
}
