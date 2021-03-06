package com.bl.personaltagbl;


import com.bl.Constant;
import com.bl.integratebl.DrawPicture;
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
    @Autowired
    private DrawPicture drawPicture;

    @Override
    public ResultMessage updatePersonalTag(String pid,String uid, ArrayList<PictureVO> pictures) {
        ArrayList<PictureVO> guolv=new ArrayList<>();
        for(PictureVO pictureVO:pictures){
            if(!pictureVO.getUrl().contains("test")){
                guolv.add(pictureVO);
            }
            System.err.println(pictureVO);
        }
        PersonalTag personalTag=personalTagDao.searchByPidAndUid(pid,uid);
        if(personalTag==null){
            return ResultMessage.NOTEXIST;
        }
        int workGroup=personalTag.getWorkGroup().split(" ").length;
        int begin=(workGroup-1)*Constant.PictureNumPerGroup;
        ArrayList<Picture> updatePictures=personalTagTrans.transPictureToPo2(guolv,begin);
        Set<Picture> pictureSet=personalTag.getPictures();
        Set<Picture> tempSet=new HashSet<>();
//        System.err.println("begin--------------:"+begin);
//        System.err.println("picturesetsize:------------"+pictureSet.size());
//        System.out.println("picturessize :----------------"+pictures.size());
        int i=0;
        for(Picture picture:pictureSet){
            if(picture.getShunxu()>=begin){//本次修改的这一组
                if(i<guolv.size()){
                    tempSet.add(updatePictures.get(i));
                    i++;
                }
            }else{//之前的组别
                tempSet.add(picture);
            }
        }
        pictureSet.clear();
        pictureSet.addAll(tempSet);
//        System.out.println(personalTag.toString());
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
            personalTag.setResulturl(new ArrayList<>());
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

                ArrayList<PictureVO> pictureVOS=personalTagTrans.transPictureToVo(personalTag.getPictures());
                ArrayList<String> resultUrls=drawPicture.drawPictures(pictureVOS,pid+"res");
                for(String s:resultUrls){
                    System.out.println(s);
                }
                personalTag.setResulturl(resultUrls);
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
                userBLService.updateCredits(uid,projectBLService.viewPro(pid).getPro_type(),projectBLService.viewPro(pid).getPoints(),points,personalTag.getStartTime());
                userBLService.updateQuality(uid,projectBLService.viewPro(pid).getPro_type(),personalTag.getQuality(),personalTag.getStartTime());
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
            PersonalTagVO personalTagVO=personalTagTrans.transPoToVoPart(personalTag);
            ArrayList<PictureVO> pictureVOS=personalTagVO.getPictures();
            int random=(int)(Math.random()*(15));
            String testUrl=Constant.testUrls[random];
            PictureVO pictureVO=new PictureVO();
            pictureVO.setUrl(testUrl);
            pictureVOS.add(pictureVO);
            personalTagVO.setPictures(pictureVOS);
            return personalTagVO;
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

    @Override
    public List<String> requesterCheckAllWork(String username, String projectID) {
        PersonalTag personalTag=personalTagDao.searchByPidAndUid(projectID,username);
        if(!personalTag.getState().equals(ProjectState.TAGING)){
            return personalTag.getResulturl();
        }else{
            return null;
        }
    }

    @Override
    public int getWorkGroup(String pid, String uid) {
        return personalTagDao.searchByPidAndUid(pid,uid).getWorkGroup().split(" ").length;
    }
}
