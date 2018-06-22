package com.bl.project;

import com.bl.Constant;
import com.blservice.MessageBLService;
import com.blservice.PersonalTagBLService;
import com.blservice.ProjectBLService;
import com.blservice.UserBLService;
import com.dao.PersonalTagDao;
import com.dao.ProjectDao;
import com.dao.ProjectStatisticsDao;
import com.dao.SimilarityDao;
import com.enums.*;
import com.model.PersonalTag;
import com.model.Project;
import com.model.ProjectStatistics;
import com.model.Similarity;
import com.util.TransSetToArray;
import com.vo.personaltagvo.CombineResVO;
import com.vo.personaltagvo.UidAndPoints;
import com.vo.projectvo.*;
import com.vo.tag.PersonalTagVO;
import com.vo.uservo.ProBriefInfo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class ProjectBL implements ProjectBLService {
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private ProjectTransVOPO projectTransVOPO;
    @Autowired
    private PersonalTagBLService personalTagBLService;
    @Autowired
    private UserBLService userBLService;
    @Autowired
    private MessageBLService messageBLService;
    @Autowired
    private ProjectStatisticsDao projectStatisticsDao;
    @Autowired
    private TransSetToArray transSetToArray;
    @Autowired
    private SimilarityDao similarityDao;

    private List<Project> waitToCheck=new ArrayList<>();

    @Override
    public JSONArray getProjectListForCommonUser() {
        JSONArray jsonArray=new JSONArray();
        List<Project> projects=projectDao.findAll();
        for(Project project:projects){
            ProjectBasic projectBasic=projectTransVOPO.transProjectToProjectBasic(project);
            JSONObject jsonObject=JSONObject.fromObject(projectBasic);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public String addPro(String username){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String currentTime=df.format(new Date());// new Date()为获取当前系统时间
        String[] res=currentTime.split(" ");
        String[] date=res[0].split("-");
        String[] time=res[1].split(":");
        return date[0]+date[1]+date[2]+time[0]+time[1]+time[2]+"-"+username;
    }

    @Override
    public ResultMessage savePro(UploadProVO uploadProVO) {
        try{
            Project projectPO=projectTransVOPO.transUploadProVOToProject(uploadProVO);
            if(!projectDao.existsById(uploadProVO.getPro_ID())){
                projectDao.saveAndFlush(projectPO);
            }else{
                projectDao.delete(projectDao.getOne(uploadProVO.getPro_ID()));
                projectDao.saveAndFlush(projectPO);
            }
            return ResultMessage.SUCCESS;
        }catch(NullPointerException e){
            return ResultMessage.NULLPOINTER;
        }catch(Exception e){
            return ResultMessage.FAIL;
        }

    }

    @Override
    public double predictPrice(int PictureNum) {
        return PictureNum*Constant.RectanglePerPicture*Constant.PricePerRectangle;
    }

    @Override
    public ResultMessage uploadPro(UploadProVO uploadProVO) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String currentTime=df.format(new Date());// new Date()为获取当前系统时间
//        try {
            //检查发起者的积分是否充足
            if(uploadProVO.getPoints()>userBLService.getCredits(uploadProVO.getPro_requester())){
                return ResultMessage.CREDITNOTENOUGH;
            }
            Project projectPO = projectTransVOPO.transUploadProVOToProject(uploadProVO);
            projectPO.setReleaseTime(new Date());
            projectPO.setPro_state(ProjectState.REALEASED);
            if (!projectDao.existsById(uploadProVO.getPro_ID())) {
                userBLService.updateExperience(uploadProVO.getPro_requester(),uploadProVO.getPoints());
                userBLService.NewRelease(uploadProVO.getPro_requester(),uploadProVO.getPro_type());
//                userBLService.updateCredits(uploadProVO.getPro_requester(),uploadProVO.getPro_type(),uploadProVO.getPoints()*(-1));
                messageBLService.generateMessage(uploadProVO.getPro_requester(),"您已发布项目"+uploadProVO.getPro_ID(),uploadProVO.getPro_ID());
                projectDao.saveAndFlush(projectPO);
            } else {
                projectDao.saveAndFlush(projectPO);
            }
        ProjectStatistics projectStatistics=projectStatisticsDao.getOne(currentTime);
        switch (uploadProVO.getPro_type()){
            case ANIMALTAG:
                projectStatistics.setReleasedAnimalNum(projectStatistics.getReleasedAnimalNum()+1);
                projectStatistics.setWaitUndertakeAnimalNum(projectStatistics.getWaitUndertakeAnimalNum()+1);
                break;
            case SCENETAG:
                projectStatistics.setReleasedSceneNum(projectStatistics.getReleasedSceneNum()+1);
                projectStatistics.setWaitUndertakeSceneNum(projectStatistics.getWaitUndertakeSceneNum()+1);
                break;
            case PERSONTAG:
                projectStatistics.setReleasedPersonNum(projectStatistics.getReleasedPersonNum()+1);
                projectStatistics.setWaitUndertakePersonNum(projectStatistics.getWaitUndertakePersonNum()+1);
                break;
            case GOODSTAG:
                projectStatistics.setReleasedGoodsNum(projectStatistics.getReleasedGoodsNum()+1);
                projectStatistics.setWaitUndertakeGoodsNum(projectStatistics.getWaitUndertakeGoodsNum()+1);
                break;
            case OTHERSTAG:
                projectStatistics.setReleasedOthersNum(projectStatistics.getReleasedOthersNum()+1);
                projectStatistics.setWaitUndertakeOthersNum(projectStatistics.getWaitUndertakeOthersNum()+1);
                break;
        }
        projectStatisticsDao.saveAndFlush(projectStatistics);
            return ResultMessage.SUCCESS;
//        }catch(NullPointerException e){
//            return ResultMessage.NULLPOINTER;
//        }catch(Exception e){
//            e.printStackTrace();
//            return ResultMessage.FAIL;
//        }
    }

    @Override
    public ResultMessage addCredits(String username, String projectID, double credits) {
        try{
            //检查用户的剩余积分是否足够
            if(credits>userBLService.getCredits(username)){
                return ResultMessage.CREDITNOTENOUGH;
            }
            //更新用户的剩余积分
            //更新项目的积分
            Project project=projectDao.getOne(projectID);
            userBLService.updateCredits(username,project.getPro_type(),credits*(-1));
            project.setPoints(project.getPoints()+credits);
            project.setZhuijiaPoints(credits);
            projectDao.saveAndFlush(project);
            //发消息给发起者和承包者
            messageBLService.generateMessage(username,"您已成功为项目"+projectID+"追加积分"+credits,projectID);
            messageBLService.generateMessage(project.getWorkerList(),"项目"+projectID+"积分增加"+credits,projectID);
            return ResultMessage.SUCCESS;
        }catch (Exception e){
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ResultMessage removePro(String username, String pid) {
        //将项目从草稿箱移除到回收站
        try{
        Project projectPO=projectDao.getOne(pid);
        projectPO.setPro_state(ProjectState.RECYCLE);
        //projectDao.deleteById(pid);
        projectDao.saveAndFlush(projectPO);
        return ResultMessage.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ResultMessage delPro(String username, String pid) {
        //将项目从回收站删除
        try{
            //Project projectPO=projectDao.getOne(pid);
            projectDao.deleteById(pid);
            return ResultMessage.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ResultMessage recoverPro(String username, String pid) {
        //将项目从回收站移到草稿箱
        try{
            Project projectPO=projectDao.getOne(pid);
            projectPO.setPro_state(ProjectState.DRAFT);
            //projectDao.deleteById(pid);
            projectDao.saveAndFlush(projectPO);
            return ResultMessage.SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultMessage.FAIL;
        }
    }

    @Override
    public ProjectVO viewPro(String pid) {
//        try{
        ProjectVO projectVO;
        Optional<Project> p=projectDao.findById(pid);
        if(!p.isPresent()){
            return null;
        }else{
            projectDao.addClickNum(pid);
            projectVO=projectTransVOPO.transProjectToProjectVO(projectDao.getOne(pid));
        }
        return projectVO;
//        }catch (Exception e){
//            return null;
//        }
    }

    @Override
    public ResultMessage choosePro(String workerID, String pid){
        try{
            Project projectPO=projectDao.getOne(pid);
            Set<String> workers=projectPO.getWorkerList();
            workers.add(workerID);
            projectPO.setWorkerList(workers);
            int groupIndex=projectPO.getAllocationIndex();
            if(calculateGroup(projectPO.getUrls().size())!=0){
                projectPO.setAllocationIndex((groupIndex+1)%calculateGroup(projectPO.getUrls().size()));
            }else{
                projectPO.setAllocationIndex(0);
            }
            personalTagBLService.addPersonalTag(pid,workerID,this.allocateUrl(groupIndex,projectPO.getUrls()),groupIndex);
            projectDao.saveAndFlush(projectPO);
            modiSimi(pid);
            userBLService.NewContract(workerID,projectPO.getPro_type());
            messageBLService.generateMessage(workerID,"您已选择项目"+pid,pid);
            messageBLService.generateMessage(projectPO.getPro_requester(),workerID+" 已经承包项目"+pid,pid);
            return ResultMessage.SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
            return ResultMessage.FAIL;
        }
    }

    private int calculateGroup(int urlSize){
        int NumPerGroup= Constant.PictureNumPerGroup;
        int totalGroup=urlSize/NumPerGroup;
        int left=urlSize%NumPerGroup;
        if(left>(NumPerGroup/2)){//如果剩下的图片不到每组图片的一半，则归到上一组,否则独自一组
            totalGroup++;
        }
        return totalGroup;
    }

    @Override
    public int getNextGroupIndex(String projectID,String workGroup){
        Project project=projectDao.getOne(projectID);
        int totalGroup=this.calculateGroup(project.getUrls().size());
        int group=project.getAllocationIndex();
        String[] already=workGroup.split(" ");
        if(already.length==totalGroup){
            //该用户已经标注过项目中的所有图片
            return -1;
        }else{//给用户分配一组没做过的图
            for (int k=0;k<totalGroup;k++){
                if(alreadyIn(already,String.valueOf(group))){//用户标注过这组图片，换下一组
                    group=(group+1)%totalGroup;
                }else{
                    return group;
                }
            }
        }
        return 0;
    }
    @Override
    public String[] getNextGroupUrl(String projectID,int group) {
        Project project=projectDao.getOne(projectID);
        String[] result=allocateUrl(group,project.getUrls());
        group++;
        if(calculateGroup(project.getUrls().size())!=0){
            project.setAllocationIndex(group%calculateGroup(project.getUrls().size()));
        }else{
            project.setAllocationIndex(0);
        }
        projectDao.saveAndFlush(project);
        return result;
    }

    private boolean alreadyIn(String[] already,String s){
        for(String ss:already){
            if(ss.equals(s)){
                return true;
            }
        }
        return false;
    }

    private String[] allocateUrl(int groupIndex,List<String> urls){
        int NumPerGroup=Constant.PictureNumPerGroup;
        if(urls.size()<Constant.MinimalNumToDivide){
            return transSetToArray.transListTOArray(urls);
        }
        int totalGroup=this.calculateGroup(urls.size());
        String[] result;
        if(groupIndex !=(totalGroup-1)){//不是最后一组
            result=new String[NumPerGroup];
            for(int i=0;i<NumPerGroup;i++){
                result[i]=urls.get(groupIndex *NumPerGroup+i);
            }
        }else{
            result=new String[urls.size()-NumPerGroup*groupIndex];
            for(int i=0;i<result.length;i++){
                result[i]=urls.get(groupIndex *NumPerGroup+i);
            }
        }
        return result;
    }

    @Override
    public ResultMessage submitPro(String workerID, String pid) {
       try{
           Project project=projectDao.getOne(pid);
           Set<String> finishedList=project.getFinished_list();
           finishedList.add(workerID);
           project.setFinished_list(finishedList);
           projectDao.saveAndFlush(project);
           personalTagBLService.submitPersonlTag(pid,workerID);
           messageBLService.generateMessage(workerID,"您已提交项目"+pid,pid);
           messageBLService.generateMessage(project.getPro_requester(),workerID+" 已提交项目"+pid,pid);
           return ResultMessage.SUCCESS;
       }catch(Exception e){
           e.printStackTrace();
           return ResultMessage.FAIL;
       }
    }

    @Override
    public ResultMessage quitPro(String workerID, String pid) {
        Project project=projectDao.getOne(pid);
        projectDao.deleteById(pid);
        Set<String> workerList=project.getWorkerList();
        workerList.remove(workerID);
        project.setWorkerList(workerList);
        project.setQuitNum(project.getQuitNum()+1);
        projectDao.saveAndFlush(project);
        modiSimi(pid);
        personalTagBLService.delPersonalTag(pid,workerID);
        messageBLService.generateMessage(workerID,"您放弃了项目"+pid,pid);
        messageBLService.generateMessage(project.getPro_requester(),workerID+" 放弃了项目"+pid,pid);
        return ResultMessage.SUCCESS;
    }

    @Override
    public double getPoints(String pid, String uid) {
        return personalTagBLService.showPersonalTagBySomeOne(pid,uid).getPoints();
    }

    @Override
    public ResultMessage saveCombineRes(CombineResVO combineResVO) {
        Project project=projectDao.getOne(combineResVO.getPid());
        project.setCombineRes_urls(combineResVO.getUrl());
        for(UidAndPoints a:combineResVO.getUidAndPoints()){
            personalTagBLService.updateQualityAndPoints(combineResVO.getPid(),a.getUid(),a.getPoints(),a.getPoints());
        }
        projectDao.saveAndFlush(project);
        return ResultMessage.SUCCESS;
    }

    @Override
    public List<String> showCombineRes(String pid) {
        return projectDao.getOne(pid).getCombineRes_urls();
    }

    public ArrayList<ProBriefInfo> getRelease(String username){
        List<Project> projects=projectDao.findByUser(username);
        ArrayList<ProBriefInfo> results=new ArrayList<>();
        for(Project p:projects){
            results.add(new ProBriefInfo(p.getPro_ID(),p.getPro_name(),p.getBrief_intro(),p.getPro_state(),
                    p.getPro_type(),p.getWorkerList().size(),p.getRemainTime(),p.getPoints()));
        }
        return results;
    }

    @Override
    public ProBriefInfo getBriefInfo(String pid) {
        Project project=projectDao.getOne(pid);
        return new ProBriefInfo(pid,project.getPro_name(),project.getBrief_intro(),project.getPro_state(),
                project.getPro_type(),project.getWorkerList().size(),project.getRemainTime(),project.getPoints());
    }

    @Override
    public ResultMessage changeToFinish(String pid) {
        Project project=projectDao.getOne(pid);
        project.setPro_state(ProjectState.FINISHED);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String currentTime=df.format(new Date());// new Date()为获取当前系统时间
        ProjectStatistics projectStatistics=projectStatisticsDao.getOne(currentTime);
        switch (project.getPro_type().toString()){
            case "ANIMALTAG":
                projectStatistics.setWaitUndertakeAnimalNum(projectStatistics.getWaitUndertakeAnimalNum()-1);
                projectStatistics.setFinishedAnimalNum(projectStatistics.getFinishedAnimalNum()+1);
                break;
            case "SCENETAG":
                projectStatistics.setWaitUndertakeSceneNum(projectStatistics.getWaitUndertakeSceneNum()-1);
                projectStatistics.setFinishedSceneNum(projectStatistics.getFinishedSceneNum()+1);
                break;
            case "PERSONTAG":
                projectStatistics.setWaitUndertakePersonNum(projectStatistics.getWaitUndertakePersonNum()-1);
                projectStatistics.setFinishedPersonNum(projectStatistics.getFinishedPersonNum()+1);
                break;
            case "GOODSTAG":
                projectStatistics.setWaitUndertakeGoodsNum(projectStatistics.getWaitUndertakeGoodsNum()-1);
                projectStatistics.setFinishedGoodsNum(projectStatistics.getFinishedGoodsNum()+1);
                break;
            case "OTHERSTAG":
                projectStatistics.setWaitUndertakeOthersNum(projectStatistics.getWaitUndertakeOthersNum()-1);
                projectStatistics.setFinishedOthersNum(projectStatistics.getFinishedOthersNum()+1);
                break;
        }
        projectStatisticsDao.saveAndFlush(projectStatistics);
        projectDao.saveAndFlush(project);
        return ResultMessage.SUCCESS;
    }

    // 过滤特殊字符
    private static String stringFilter(String str) throws PatternSyntaxException {
        // 只允许字母和数字  String regEx ="[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx="[`~!@#$%^&*()+=|{}':;'\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        return m.replaceAll("").trim();
    }

    @Override
    public ArrayList<ProjectBasic> searchPro(String keywords, ProjectType type, SearchProState state, PointsRange pointsRange, NumberOfDays numberOfDays) {
        ArrayList<ProjectBasic> res=new ArrayList<>();
        List<Project> list=projectDao.findAll();
        List<String> allString=new ArrayList<>();
        for(Project p:list){
            allString.add(p.getPro_ID());
        }

        //根据项目类型查找
        List<String> list2=new ArrayList<>();
        if(type.equals(ProjectType.All)){
            list2.addAll(allString);
        }else{
            list2=projectDao.searchProByType(type);
        }

        //根据项目状态查找
        List<String> list3=new ArrayList<>();
        if(state.equals(SearchProState.All)){
            list3.addAll(allString);
        }else{
            switch (state){
                case Underway:
                    list3=projectDao.searchProIDByState(ProjectState.REALEASED);
                    break;
                case Examine:
                    list3=projectDao.searchProIDByState(ProjectState.EXAMINE);
                    break;
                case Finished:
                    list3=projectDao.searchProIDByState(ProjectState.FINISHED);
                    break;
            }
        }

        //根据积分范围查找
        List<String> list4=new ArrayList<>();
        if(pointsRange.equals(PointsRange.All)){
            list4.addAll(allString);
        }else{
            list4=projectDao.searchProByPointRange(pointsRange.getMin(),pointsRange.getMax());

        }

        //根据剩余天数查找
        List<String> list5=new ArrayList<>();
        if(numberOfDays.equals(NumberOfDays.All)){
            list5.addAll(allString);
        }else{
            list5=projectDao.searchProByNumberOfDays(numberOfDays.getMin(),numberOfDays.getMax());
        }

//        List<String> tempList1=getIntersection(list1,list2);
        List<String> tempList2=getIntersection(list2,list3);
        List<String> tempList3=getIntersection(tempList2,list4);
        List<String> tempList4=getIntersection(tempList3,list5);

        //根据关键词查找
        //文本相关性算法 BM25算法
        //排序公式 0.7*文本相关性+0.2*积分+0.1*点击量
        //在文本相关性中，项目名占比0.6，项目简介占比0.4
        List<String> list1=new ArrayList<>();
        if(keywords.equals("")){
            //没输入关键词
            for(String a:tempList4){
                Project p=projectDao.getOne(a);
                ProjectBasic projectBasic=projectTransVOPO.transProjectToProjectBasic(p);
                res.add(projectBasic);
            }
            return res;
        }else{
            //过滤特殊字符
            keywords=stringFilter(keywords);
            if(keywords.equals("")){
                //输入了关键词，经过过滤后找不到，则返回空数组
                ArrayList<ProjectBasic> projectBasics=new ArrayList<>();
                return projectBasics;
            }else{
                //将关键句分词
                String[] k=keywords.split("[、， ,]");
                //存储的是项目的id和综合评分
                Map<String,Double> map=new HashMap<>();
                //文档集合总数N
                int totalNum=tempList4.size();
                double k1=1.2;
                double k2=200;
                double b=0.75;
                double avgNameNum=projectDao.avgnameNum(new ArrayList<>(tempList4));//项目中名字的平均字数
                double avgBriefIntroNum=projectDao.avgBriefIntroNum(new ArrayList<>(tempList4));//项目中简介的平均字数

                for(String a:tempList4){
                    boolean tag=false;
                    Project project=projectDao.getOne(a);
                    double score=0.0;
                    double temp=0.0;
                    for(int i=0;i<k.length;i++){
                        //文档名字集合中包含关键字的文档个数
                        boolean flag=false;
                        int nameNum=projectDao.countNameNum(k[i]);
                        if(project.getPro_name().contains(k[i])){
                            tag=true;
                            flag=true;
                        }
                        //该项目名称的字数
                        double name_dl=projectDao.nameNum(a);
                        double name_k=k1*((1-b)+b*name_dl/avgNameNum);
                        double temp1=Math.log((totalNum-nameNum+0.5)/(nameNum+0.5))*((k1+1)/(name_k+1));
                        //文档简介集合中包含关键字的文档个数
                        int briefNum=projectDao.countBriefIntroNum(k[i]);
                        if(project.getBrief_intro().contains(k[i])){
                            tag=true;
                            flag=true;
                        }
                        double brief_dl=projectDao.countBriefIntroNum(k[i]);
                        double brief_k=k1*((1-b)+b*brief_dl/avgBriefIntroNum);
                        double temp2=Math.log((totalNum-briefNum+0.5)/(briefNum+0.5))*(k1+1)/(brief_k+1);
                        if(flag){
                            temp=temp+0.6*temp1+0.4*temp2;
                        }

                    }
                    if(tag){
                        score=0.7*temp+0.02*project.getPoints()+0.1*project.getClickNum();
                        System.err.println("pro_id:"+a+"----------temp:"+temp+"----------score:"+score);
                        map.put(a,score);
                    }
                }
                //根据关键词没能找到相关项目
                if(map.isEmpty()){
                    ArrayList<ProjectBasic> projectBasics=new ArrayList<>();
                    return projectBasics;
                }
                List<Map.Entry<String,Double>> sort=new ArrayList<>(map.entrySet());
                Collections.sort(sort, new Comparator<Map.Entry<String, Double>>() {
                    @Override
                    public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                        //降序排序
                        return o2.getValue().compareTo(o1.getValue());
                    }
                });
                for(Map.Entry<String,Double> mapping:sort){
                    Project p=projectDao.getOne(mapping.getKey());
                    ProjectBasic projectBasic=projectTransVOPO.transProjectToProjectBasic(p);
                    res.add(projectBasic);
                }
                return res;
            }

        }

    }

    private String calLastMonth(String currentMonth){
        String year=currentMonth.split("-")[0];
        String month=currentMonth.split("-")[1];
        String lastMonth;
        if(month.equals("01")) {//新的一年
            int lastYear=Integer.parseInt(year)-1;
            lastMonth=lastYear+"-"+"12";
        }else{
            switch (month) {
                case "12":
                    lastMonth = year + "-" + 11;
                    break;
                case "11":
                    lastMonth = year + "-" + 10;
                    break;
                case "10":
                    lastMonth = year + "-09";
                    break;
                default:
                    int temp = Integer.parseInt(month.substring(1, 2)) - 1;
                    lastMonth = year + "-0" + temp;
                    break;
            }
        }
        return lastMonth;
    }

    @Scheduled(cron = "0 0 0 1 * ?" )//每月一号00：00：00 新建统计数据的列,并清算上季度数据
    public void initStatistics(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String currentTime=df.format(new Date());// new Date()为获取当前系统时间
        String lastMonth=calLastMonth(currentTime);
        Optional<ProjectStatistics> projectStatistics=projectStatisticsDao.findById(lastMonth);
        ProjectStatistics newProjectStatistics=new ProjectStatistics(currentTime);
        if(!projectStatistics.isPresent()){
            //没有上个月，当前月为第一月
            projectStatisticsDao.saveAndFlush(newProjectStatistics);
        }else{
            //清算上月结余
            ProjectStatistics oldProjectStatistics=projectStatistics.get();
            newProjectStatistics.setWaitUndertakeAnimalNum(oldProjectStatistics.getWaitUndertakeAnimalNum());
            newProjectStatistics.setWaitUndertakeSceneNum(oldProjectStatistics.getWaitUndertakeSceneNum());
            newProjectStatistics.setWaitUndertakePersonNum(oldProjectStatistics.getWaitUndertakePersonNum());
            newProjectStatistics.setWaitUndertakeGoodsNum(oldProjectStatistics.getWaitUndertakeGoodsNum());
            newProjectStatistics.setWaitUndertakeOthersNum(oldProjectStatistics.getWaitUndertakeOthersNum());
            projectStatisticsDao.saveAndFlush(newProjectStatistics);
        }
    }

    @Scheduled(fixedRate=40000)//每40秒执行一次
    public void changeProject() {
        System.err.println("新的一天真开心啊~~");
        System.err.println(new Date());
        for(int i=0;i<waitToCheck.size();i++){
            Project p=waitToCheck.get(i);
            if(new Date().after(p.getDeadLine())){
                p.setPro_state(ProjectState.EXAMINE);
                projectDao.delete(p);
                projectDao.saveAndFlush(p);
                waitToCheck.remove(p);
            }
        }


    }

    @Scheduled(cron = "0 0 0 * * ?")//每天零点执行一次
    public void updateRemainDays(){
        List<Project> projects=projectDao.findAll();
        for(Project project:projects){
            if(project.getPro_state().equals(ProjectState.REALEASED)){
                project.setRemainTime(project.getRemainTime()-1);
                projectDao.saveAndFlush(project);
                if(project.getRemainTime()==0){
                    waitToCheck.add(project);
                }
            }
        }
    }

    @Override
    //根据项目推荐相似项目
    public ArrayList<Recommend2> recommendSimiPro(String pid,String uid){
        ArrayList<Recommend2> a=new ArrayList<>();
        ArrayList<ProBriefInfo> proBriefInfos=userBLService.getContract(uid).getContractOn();
        ArrayList<String> pids=new ArrayList<>();
        for(ProBriefInfo p:proBriefInfos){
            pids.add(p.getPid());
        }
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.REALEASED);
        ArrayList<Similarity> getAllSimi=similarityDao.getAllSimi(pid);
        ArrayList<Recommend2> res=new ArrayList<>();
        Map<String,Double> map=new HashMap<>();
        for(Similarity s:getAllSimi){
            if(s.getPid1().equals(pid)){
                map.put(s.getPid2(),s.getSimilarity());
            }else{
                map.put(s.getPid1(),s.getSimilarity());
            }
        }
        List<Map.Entry<String,Double>> sort=new ArrayList<>(map.entrySet());
        Collections.sort(sort, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                //降序排序
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for(Map.Entry<String,Double> mapping:sort){
            Project p=projectDao.getOne(mapping.getKey());
            Recommend2 recommend2=new Recommend2(p);
            res.add(recommend2);
        }

        for(Recommend2 r:res){
            if(!pids.contains(r.getPid())){
                a.add(r);
            }
        }
        return a;

    }

    @Override
    public ArrayList<FinishCondition> showFinishConditionList(String pid) {
        ArrayList<FinishCondition> res=new ArrayList<>();
        Set<String> workers=projectDao.getOne(pid).getWorkerList();
        for(String w:workers){
            PersonalTagVO personalTagVO=personalTagBLService.showPersonalTagBySomeOne(pid,w);
            long spendTime=(personalTagVO.getSubmitTime().getTime()-personalTagVO.getStartTime().getTime())/1000/60;
            FinishCondition finishCondition=new FinishCondition(0,w,personalTagVO.getSubmitTime(),spendTime);
            res.add(finishCondition);
        }

        Collections.sort(res, new Comparator<FinishCondition>() {
            @Override
            public int compare(FinishCondition o1, FinishCondition o2) {
                return (int) (o1.getSpendTime()-o2.getSpendTime());
            }
        });
        for(int i=0;i<res.size();i++){
            res.get(i).setCixu(i+1);
        }
        return res;
    }

    @Override
    public ArrayList<Recommend1> newestPro() {
        ArrayList<Recommend1> res=new ArrayList<>();
        List<Project> allPros=projectDao.findAll();
        Collections.sort(allPros, new Comparator<Project>() {
            @Override
            public int compare(Project o1, Project o2) {
                return (int)(o2.getReleaseTime().getTime()-o1.getReleaseTime().getTime());
            }
        });
        for(Project a:allPros){
            res.add(new Recommend1(a));
        }
        return res;
    }


    //为用户推荐项目
    @Override
    public ArrayList<Recommend1> recommendPro(String uid) {
        ArrayList<Recommend1> res=new ArrayList<>();
        ArrayList<ProBriefInfo> proBriefInfos=userBLService.getContract(uid).getContractOn();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.REALEASED);
        Map<String,Double> list=new HashMap<>();
        ArrayList<String> pids=new ArrayList<>();
        for(ProBriefInfo p:proBriefInfos){
            pids.add(p.getPid());
        }
        //计算推荐值
        for(Project pr:projects){
            double score=0.0;
            for(ProBriefInfo p:proBriefInfos){
                if(!pr.getPro_ID().equals(p.getPid())){
                    score=score+similarityDao.showSimi(pr.getPro_ID(),p.getPid()).getSimilarity();
                }
            }
            if(!pids.contains(pr.getPro_ID())){
                list.put(pr.getPro_ID(),score);
            }
        }
        List<Map.Entry<String,Double>> sort=new ArrayList<>(list.entrySet());
        Collections.sort(sort, new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                //降序排序
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for(Map.Entry<String,Double> mapping:sort){
            Project p=projectDao.getOne(mapping.getKey());
            Recommend1 recommend1=new Recommend1(p);
            res.add(recommend1);
        }
        return res;
    }

    //修改后端存储的两两项目之间的相似度
    private void modiSimi(String pid){
        ArrayList<String> underwayTeam=projectDao.searchProIDByState(ProjectState.REALEASED);
        Set<String> workersSet=projectDao.getOne(pid).getWorkerList();
        List<String> workers1=new ArrayList<>(workersSet);
        for(String u:underwayTeam){
            Set<String> temp=projectDao.getOne(u).getWorkerList();
            List<String> workers2=new ArrayList<>(temp);
            double simi=calWij(workers1,workers2);
            Similarity similarity=similarityDao.showSimi(pid,u);
            if(similarity==null){
                Similarity newSimilarity=new Similarity(u,pid,simi);
                similarityDao.saveAndFlush(newSimilarity);
            }else{
                similarity.setSimilarity(simi);
                similarityDao.saveAndFlush(similarity);
            }
        }
    }

    //计算两个项目之间的同现相似度
    private double calWij(List<String> workerList1,List<String> workerList2){
        int num=getIntersection(workerList1,workerList2).size();
        double res=0.0;
        if(workerList1.size()==0||workerList2.size()==0){
            res=0;
        }else{
            res=num/Math.sqrt(workerList1.size()*workerList2.size());
        }
        return res;
    }

    private static List<String> getIntersection(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        for (String s : list2) {//遍历list1
            if (list1.contains(s)) {//如果存在这个数
                result.add(s);//放进一个list里面，这个list就是交集
            }
        }
        return result;
    }





    //统计信息
    @Override
    public int totalFinishedNum() {
        return projectDao.finishNum(ProjectState.FINISHED);
    }

    @Override
    public int totalReleasedNum() {
        return projectDao.sum();
    }

    @Override
    public Map<String, Integer> releasedPerMonth(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedAnimalNum()+p.getReleasedSceneNum()+p.getReleasedGoodsNum()+p.getReleasedPersonNum()+p.getReleasedOthersNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakePerMonth(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakeAnimalNum()+p.getWaitUndertakeGoodsNum()+p.getWaitUndertakeOthersNum()+p.getWaitUndertakePersonNum()+p.getWaitUndertakeSceneNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedPerMonth(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedAnimalNum()+p.getFinishedGoodsNum()+p.getFinishedOthersNum()+p.getFinishedPersonNum()+p.getFinishedSceneNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> releasedAnimalNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedAnimalNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> releasedSceneNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedSceneNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> releasedPersonNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedPersonNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> releasedGoodsNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedGoodsNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> releasedOthersNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getReleasedOthersNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakeAnimalNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakeAnimalNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakeSceneNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakeSceneNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakePersonNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakePersonNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakeGoodsNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakeGoodsNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> waitUndertakeOthersNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getWaitUndertakeOthersNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedAnimalNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedAnimalNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedSceneNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedSceneNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedPersonNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedPersonNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedGoodsNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedGoodsNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String, Integer> finishedOthersNum(String year) {
        ArrayList<ProjectStatistics> projectStatistics=projectStatisticsDao.findInYear(year);
        Map<String,Integer> res=new HashMap<>();
        if(projectStatistics!=null){
            for(ProjectStatistics p:projectStatistics){
                res.put(p.getYearAndMonth().split("-")[1],p.getFinishedOthersNum());
            }
        }
//        res=completeInt(res);
        return res;
    }

    @Override
    public Map<String,String> avgReleasedNum(String year){
        Map<String,String> res=new HashMap<>();
        DecimalFormat df = new DecimalFormat("##0.00");

        double animal=projectStatisticsDao.calAvgReleasedAnimalNum(year);
        res.put(ProjectType.ANIMALTAG.getChinese(),df.format(animal));

        double good=projectStatisticsDao.calAvgReleasedGoodsNum(year);
        res.put(ProjectType.GOODSTAG.getChinese(),df.format(good));

        double other=projectStatisticsDao.calAvgReleasedOthersNum(year);
        res.put(ProjectType.OTHERSTAG.getChinese(),df.format(other));

        double personal=projectStatisticsDao.calAvgReleasedPersonNum(year);
        res.put(ProjectType.PERSONTAG.getChinese(),df.format(personal));

        double scene=projectStatisticsDao.calAvgReleasedSceneNum(year);
        res.put(ProjectType.SCENETAG.getChinese(),df.format(scene));

        return res;
    }

    private int calDays(Date day1,Date day2){
        return Math.abs((int)(day1.getTime()-day2.getTime())/(1000*3600*24));
    }

    @Override
    public ArrayList<SanDianTuVO> picNumAndFinishTimeToSatisfy() {
        ArrayList<SanDianTuVO> res=new ArrayList<>();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.FINISHED);
        for(Project p:projects){
            SanDianTuVO sanDianTuVO=new SanDianTuVO(p.getUrls().size(),calDays(p.getDeadLine(),p.getReleaseTime()),p.getSatisfy());
            res.add(sanDianTuVO);
        }
        return res;
    }

    @Override
    public ArrayList<SanDianTuVO> picNumAndContractNumToSatisfy() {
        ArrayList<SanDianTuVO> res=new ArrayList<>();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.FINISHED);
        for(Project p:projects){
            SanDianTuVO sanDianTuVO=new SanDianTuVO(p.getUrls().size(),p.getWorkerList().size(),p.getSatisfy());
            res.add(sanDianTuVO);
        }
        return res;
    }

    @Override
    public ArrayList<SanDianTuVO> picNumAndFinishTimeToGiveUp() {
        ArrayList<SanDianTuVO> res=new ArrayList<>();
        List<Project> projects=projectDao.findAll();
        for(Project p:projects){
            SanDianTuVO sanDianTuVO=new SanDianTuVO(p.getUrls().size(),calDays(p.getDeadLine(),p.getReleaseTime()),p.getQuitNum());
            res.add(sanDianTuVO);
        }
        return res;
    }

    @Override
    public ArrayList<SanDianTuVO> picNumAndFinishTimeToComplete() {
        ArrayList<SanDianTuVO> res=new ArrayList<>();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.FINISHED);
        for(Project p:projects){
            ArrayList<String> workers=new ArrayList<>(projectDao.getOne(p.getPro_ID()).getWorkerList());
            double sum=0;
            for(String w:workers){
                sum=sum+(double) personalTagBLService.getWorkGroup(p.getPro_ID(),w)/(double)(p.getUrls().size()/Constant.PictureNumPerGroup);
            }
            SanDianTuVO sanDianTuVO;
            if(workers.size()==0){
                sanDianTuVO=new SanDianTuVO(p.getUrls().size(),calDays(p.getDeadLine(),p.getReleaseTime()),0.0);
            }else{
                sanDianTuVO=new SanDianTuVO(p.getUrls().size(),calDays(p.getDeadLine(),p.getReleaseTime()),sum/workers.size());
            }
            res.add(sanDianTuVO);
        }
        return res;
    }

    @Override
    public Map<String, Double> picNumToPoints() {
        Map<String,Double> res=new HashMap<>();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.FINISHED);
        int tag1=0,tag2=0,tag3=0,tag4=0,tag5=0;
        double sum1=0,sum2=0,sum3=0,sum4=0,sum5=0;
        for(Project p:projects){
            if(p.getUrls().size()<=500){
                tag1++;
//                System.err.println("--------"+predictPrice(PicNum.UNDER_500,calDays(p.getDeadLine(),p.getReleaseTime())));
                sum1=sum1+p.getPoints()-predictPrice(p.getUrls().size());
            }else if(p.getUrls().size()>=500&&p.getUrls().size()<=1000){
                tag2++;
                sum2=sum2+p.getPoints()-predictPrice(p.getUrls().size());
            }else if(p.getUrls().size()>=1000&&p.getUrls().size()<=2000){
                tag3++;
                sum3=sum3+p.getPoints()-predictPrice(p.getUrls().size());
            }else if(p.getUrls().size()>=2000&&p.getUrls().size()<=3000){
                tag4++;
                sum4=sum4+p.getPoints()-predictPrice(p.getUrls().size());
            }else if(p.getUrls().size()>=3000){
                tag5++;
                sum5=sum5+p.getPoints()-predictPrice(p.getUrls().size());
            }
        }
        String[] a=Constant.picRange;
        if(tag1==0){
            res.put(a[0],0.0);
        }else{
            res.put(a[0],sum1/tag1);
        }
        if(tag2==0){
            res.put(a[1],0.0);
        }else{
            res.put(a[1],sum2/tag2);
        }
        if(tag3==0){
            res.put(a[2],0.0);
        }else{
            res.put(a[2],sum3/tag3);
        }
        if(tag4==0){
            res.put(a[3],0.0);
        }else{
            res.put(a[3],sum4/tag4);
        }
        if(tag5==0){
            res.put(a[4],0.0);
        }else{
            res.put(a[4],sum5/tag5);
        }
        return res;
    }

    @Override
    public Map<String, Double> picNumToAvgPoints() {
        Map<String,Double> res=new HashMap<>();
        ArrayList<Project> projects=projectDao.searchProjectByState(ProjectState.FINISHED);
        int tag1=0,tag2=0,tag3=0,tag4=0,tag5=0;
        double sum1=0,sum2=0,sum3=0,sum4=0,sum5=0;
        for(Project p:projects){
            if(p.getUrls().size()<=500){
                if(p.getZhuijiaPoints()!=0){
                    tag1++;
                    sum1=sum1+p.getZhuijiaPoints();
                }
            }else if(p.getUrls().size()>=500&&p.getUrls().size()<=1000){
                if(p.getZhuijiaPoints()!=0){
                    tag2++;
                    sum2=sum2+p.getZhuijiaPoints();
                }
            }else if(p.getUrls().size()>=1000&&p.getUrls().size()<=2000){
                if(p.getZhuijiaPoints()!=0){
                    tag3++;
                    sum3=sum3+p.getZhuijiaPoints();
                }
            }else if(p.getUrls().size()>=2000&&p.getUrls().size()<=3000){
                if(p.getZhuijiaPoints()!=0){
                    tag4++;
                    sum4=sum4+p.getZhuijiaPoints();
                }
            }else if(p.getUrls().size()>=3000){
                if(p.getZhuijiaPoints()!=0){
                    tag5++;
                    sum5=sum5+p.getZhuijiaPoints();
                }
            }
        }
        String[] a=Constant.picRange;
        if(tag1==0){
            res.put(a[0],0.0);
        }else{
            res.put(a[0],sum1/tag1);
        }
        if(tag2==0){
            res.put(a[1],0.0);
        }else{
            res.put(a[1],sum2/tag2);
        }
        if(tag3==0){
            res.put(a[2],0.0);
        }else{
            res.put(a[2],sum3/tag3);
        }
        if(tag4==0){
            res.put(a[3],0.0);
        }else{
            res.put(a[3],sum4/tag4);
        }
        if(tag5==0){
            res.put(a[4],0.0);
        }else{
            res.put(a[4],sum5/tag5);
        }
        return res;
    }

    @Override
    public ResultMessage markCombineRes(String pid, int score) {
        Project project=projectDao.getOne(pid);
        project.setSatisfy(score);
        projectDao.saveAndFlush(project);
        return ResultMessage.SUCCESS;
    }


}
