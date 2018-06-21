package com.bl.userbl;

import com.bl.Constant;
import com.blservice.LogBLService;
import com.blservice.PersonalTagBLService;
import com.blservice.ProjectBLService;
import com.blservice.UserBLService;
import com.dao.ProjectStatisticsDao;
import com.dao.UserDao;
import com.enums.ProjectState;
import com.enums.ProjectType;
import com.enums.ResultMessage;
import com.enums.UserIdentity;
import com.model.BrowseRecord;
import com.model.ProjectStatistics;
import com.model.User;
import com.vo.uservo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserBL implements UserBLService {

    private final UserDao userDao;
    private final LogBLService logBLService;
    private final ProjectBLService projectBLService;
    private final PersonalTagBLService personalTagBLService;
    private final ProjectStatisticsDao projectStatisticsDao;

    @Autowired
    public UserBL(UserDao userDao, LogBLService logBLService, ProjectBLService projectBLService,
                  PersonalTagBLService personalTagBLService, ProjectStatisticsDao projectStatisticsDao) {
        this.userDao = userDao;
        this.logBLService = logBLService;
        this.projectBLService = projectBLService;
        this.personalTagBLService = personalTagBLService;
        this.projectStatisticsDao=projectStatisticsDao;
    }


    @Override
    public ResultMessage register(NewUser newUser) {
        //检索用户名是否已经被占用，避免用户名重复
        List<User> allUsers=userDao.findAll();
        for(User u:allUsers){
            if(u.getUsername().equals(newUser.getUsername())){
                return ResultMessage.EXIST;
            }
        }
        User u=new User(newUser);
        if(newUser.getUsername().equals("admin")){
            u.setIdentity(UserIdentity.ADMINISTRATOR);
        }
        if (newUser.getUsername().equals("manager")){
            u.setIdentity(UserIdentity.MANAGER);
        }
        Calendar now = Calendar.getInstance();
        int month=now.get(Calendar.MONTH)+1;
        String time;
        if(month<10){
            time=now.get(Calendar.YEAR)+"-0"+month;
        }else{
            time=now.get(Calendar.YEAR)+"-"+(now.get(Calendar.MONTH)+1);
        }
        projectStatisticsDao.newRegister(time);
        userDao.saveAndFlush(u);
        logBLService.addLog(newUser.getUsername(),"register");
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage login(LoginReq loginReq) {
        try{
            User user=userDao.searchUserById(loginReq.getUsername());
            //用户名不存在
            if(user == null){
                return ResultMessage.NOUSER;
            }
            //密码错误
            if(!user.getPassword().equals(loginReq.getPassword())){
                return ResultMessage.PASSERROR;
            }
            //用户存在且密码正确
            user.setOn(true);
            user.setDateLogin(new Date());
            userDao.saveAndFlush(user);
            logBLService.addLog(loginReq.getUsername(),"login");
            switch (user.getIdentity()){
                case COMMONUSER:
                    return ResultMessage.CommonUserLogin;
                case ADMINISTRATOR:
                    return ResultMessage.AdministratorLogin;
                case MANAGER:
                    return ResultMessage.ManagerLogin;
                default:
                    return ResultMessage.FAIL;
            }
        }catch (JpaObjectRetrievalFailureException e){
            return null;
        }

    }

    @Override
    public ResultMessage logout(String username) {
        User user=userDao.getOne(username);
        Date in=user.getDateLogin();
        Date out=new Date();
        long dTime=(out.getTime()-in.getTime())/(1000*60);
        user.setTotalDuration(dTime+user.getTotalDuration());
        user.setOn(false);
        user.setDateLogout(out);
        user.setActiveContract(user.getActiveContract()+dTime*1.0/60);
        user.setActiveRelease(user.getActiveRelease()+dTime*1.0/60);
        userDao.saveAndFlush(user);
        logBLService.addLog(username,"logout");
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage reset(PersonalCenter user) {
        User u=userDao.searchUserById(user.getUsername());
        u.setEmail(user.getEmail());
        u.setDescription(user.getDescription());
        u.setAge(user.getAge());
        u.setTelephone(user.getTelephone());
        u.setSex(user.getSex());
        StringBuilder tags= new StringBuilder();
        for(String s:user.getTags()){
            tags.append(s);
        }
        u.setTag(tags.toString());
        userDao.saveAndFlush(u);
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage resetPassword(String username, String oldPass, String newPass){
        User user=userDao.searchUserById(username);
        if(!oldPass.equals(user.getPassword())){
            return ResultMessage.PASSERROR;
        }else{
            user.setPassword(newPass);
            userDao.saveAndFlush(user);
            return ResultMessage.SUCCESS;
        }
    }

    @Override
    public PersonalCenter getPersonal(String username) {
        User user=userDao.searchUserById(username);
        PersonalCenter result=new PersonalCenter();
        result.setUsername(user.getUsername());
        result.setIdentity(user.getIdentity().toString());
        result.setEmail(user.getEmail());
        result.setCredits(user.getCredits());
        result.setTelephone(user.getTelephone());
        result.setAge(user.getAge());
        result.setSex(user.getSex());
        //计算排名
        ArrayList<String> users=userDao.ListUserByRank(UserIdentity.COMMONUSER);
        int rank=users.indexOf(user.getUsername())+1;
        result.setRank(rank);
        result.setRankRatio(1-(rank*1.0/users.size()));

        result.setExperience(user.getExperience());
        result.setDescription(user.getDescription());
        result.setQuality(user.getGeneralQuality());
        result.setTags(user.getTag().split(","));
        result.setNumRelease(user.getNum_Release());
        result.setNumContract(user.getNum_Contract());
        return result;
    }

    @Override
    public ReleaseProject getRelease(String username) {
        ArrayList<ProBriefInfo> allProject=projectBLService.getRelease(username);
        ArrayList<ProBriefInfo> drafts=new ArrayList<>();
        ArrayList<ProBriefInfo> recycles=new ArrayList<>();
        ArrayList<ProBriefInfo> on=new ArrayList<>();
        ArrayList<ProBriefInfo> examines=new ArrayList<>();
        ArrayList<ProBriefInfo> finished=new ArrayList<>();
        for(ProBriefInfo p:allProject){
            switch (p.getState()){
                case DRAFT:
                    drafts.add(p);
                    break;
                case RECYCLE:
                    recycles.add(p);
                    break;
                case REALEASED:
                    on.add(p);
                    break;
                case EXAMINE:
                    examines.add(p);
                    break;
                case FINISHED:
                    finished.add(p);
                    break;
                default:
                    break;
            }
        }
        return new ReleaseProject(username,allProject.size(),allProject,recycles,drafts,on,examines,finished);
    }

    @Override
    public ContractProject getContract(String username) {
        ArrayList<ProBriefInfo> allProject=personalTagBLService.getContract(username);
        ArrayList<ProBriefInfo> ContractOn=new ArrayList<>();//用户承包的并且正在进行中的项目ID
        ArrayList<ProBriefInfo> ContractExamine=new ArrayList<>();//用户完成标注，提交等待验收
        ArrayList<ProBriefInfo> ContractAbort=new ArrayList<>();//工人选择后，中途放弃的
        ArrayList<ProBriefInfo> ContractFinished=new ArrayList<>();//用户承包的并且工作结束
        for(ProBriefInfo p:allProject){
            switch (p.getState()){
                case TAGING:
                    ContractOn.add(p);
                    break;
                case SUBMITTED:
                    ContractExamine.add(p);
                    break;
                case FINISHED:
                    ContractFinished.add(p);
                    break;
                case GIVEUP:
                    ContractAbort.add(p);
                    break;
                default:
                    break;
            }
        }
        return new ContractProject(username,allProject.size(),allProject,ContractOn,ContractExamine,ContractAbort,ContractFinished);
    }

    @Override
    public UserStatistics getUserStatistics(String username) {
        User user=userDao.getOne(username);
        System.out.println(user.toString());
        UserStatistics result=new UserStatistics();
        result.setUsername(username);
        result.setNum_Contract(user.getNum_Contract());
        result.setNum_Release(user.getNum_Release());
        result.setRank(this.calculateRank(username));
        result.setExperience(user.getExperience());
        result.setGeneralQuality(user.getGeneralQuality());
        result.setActiveDegreeRelease(user.getActiveRelease());
        result.setActiveDegreeContract(user.getActiveContract());

        Map<String ,Integer> cons=new HashMap<>();
        ArrayList<ProBriefInfo> myContract=personalTagBLService.getContract(username);
        int on=0,examine=0,abort=0,finish=0;
        for(ProBriefInfo p:myContract){
            switch (p.getState()){
                case TAGING:
                    on++;
                    break;
                case SUBMITTED:
                    examine++;
                    break;
                case GIVEUP:
                    abort++;
                    break;
                case FINISHED:
                    finish++;
                    break;
                default:
                    break;
            }
        }
        cons.put(ProjectState.TAGING.toString(),on);
        cons.put(ProjectState.EXAMINE.toString(),examine);
        cons.put(ProjectState.GIVEUP.toString(),abort);
        cons.put(ProjectState.FINISHED.toString(),finish);
        result.setContractPerState(cons);

        Map<String ,Integer> res=new HashMap<>();
        ArrayList<ProBriefInfo> myRelease=projectBLService.getRelease(username);
        int recycle=0,draft=0,onR=0,examineR=0,finishR=0;
        for(ProBriefInfo p:myRelease){
            switch (p.getState()){
                case RECYCLE:
                    recycle++;
                    break;
                case DRAFT:
                    draft++;
                    break;
                case TAGING:
                    onR++;
                    break;
                case EXAMINE:
                    examineR++;
                    break;
                case FINISHED:
                    finish++;
                    break;
                default:
                    break;
            }
        }
        res.put(ProjectState.RECYCLE.toString(),recycle);
        res.put(ProjectState.DRAFT.toString(),draft);
        res.put(ProjectState.TAGING.toString(),onR);
        res.put(ProjectState.EXAMINE.toString(),examineR);
        res.put(ProjectState.GIVEUP.toString(),abort);
        res.put(ProjectState.FINISHED.toString(),finishR);
        result.setReleasePerState(res);

        result.setQuality(user.getQuality());
        String[] gongxian= Constant.GongXian;
        Map<Integer,Integer> gongXianPhase=user.getGongXian();
        Map<String,Integer> return_gongxian=new HashMap<>();
        for(int i=0;i<5;i++){
            return_gongxian.put(gongxian[i],gongXianPhase.get(i));
        }
        result.setGongXian(return_gongxian);
        result.setContractPerType(user.getContractTypeNum());
        result.setReleasePerType(user.getReleaseTypeNum());

        ProjectType[] types= Constant.Types;
        Map<ProjectType,Double> avgTime=user.getAvgTimePerType();
        Map<ProjectType,Double> avgCredits=user.getAvgCreditsPerType();
        Map<ProjectType,Double> chanchubi=new HashMap<>();
        for (ProjectType type : types) {
            if(avgTime.get(type)!=0){
                chanchubi.put(type, avgCredits.get(type) / avgTime.get(type));
            }else{
                chanchubi.put(type,0.0);
            }
        }
        result.setAvgTimePerType(avgTime);
        result.setAvgCreditsPerType(avgCredits);
        result.setChanchubi(chanchubi);
        return result;
    }

    //计算排名，根据经验值
    private int calculateRank(String username){
        ArrayList<String> users=userDao.ListUserByRank(UserIdentity.COMMONUSER);
        return users.indexOf(username)+1;
    }

    @Override
    public Set<BrowseRecord> getBrowseRecord(String username) {
        return userDao.getBrowseRecord(username);
    }

    @Override
    public void insertBrowseRecord(NewBrowseRecord recordVO) {
        User user=userDao.getOne(recordVO.getUsername());
        Set<BrowseRecord> records=user.getRecords();
        boolean isExist=false;
        for(BrowseRecord br:records){
            if(br.getProjectID().equals(recordVO.getProjectID())){//如果此用户在一段时间内浏览过这个项目
                isExist=true;
                br.setTimes(br.getTimes()+1);
                br.setBrowseTime(new Date());
                break;
            }
        }
        if(!isExist){
            BrowseRecord newRecord=new BrowseRecord();
            newRecord.setBrowseTime(new Date());
            newRecord.setProjectID(recordVO.getProjectID());
            newRecord.setTimes(1);
            newRecord.setType(recordVO.getType());
            records.add(newRecord);
        }
        userDao.saveAndFlush(user);
    }

    @Override
    public double getCredits(String username) {
        return userDao.searchUserById(username).getCredits();
    }

    @Override
    public ResultMessage updateCredits(String username, ProjectType type, double dValue) {
            User user=userDao.getOne(username);
            user.setCredits(user.getCredits()+dValue);
            if(dValue>0){//如果承包者获得积分，更新他该类别的平均积分
                Map<ProjectType,Double> avgCredits=user.getAvgCreditsPerType();
                double oldAvgCredit=avgCredits.get(type);
                int contractNumThisType=user.getContractTypeNum().get(type);
                avgCredits.put(type,(oldAvgCredit*(contractNumThisType-1)+dValue)/contractNumThisType);
                user.setAvgCreditsPerType(avgCredits);
            }
            userDao.saveAndFlush(user);
            return ResultMessage.SUCCESS;
    }

    //更新用户的经验值，并同时更新排名
    @Override
    public ResultMessage updateExperience(String username, double dValue) {
            User user=userDao.getOne(username);
            user.setExperience(user.getExperience()+dValue);
            userDao.saveAndFlush(user);
            return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage updateQuality(String username, ProjectType type, double gongXian) {
        User user=userDao.searchUserById(username);

        Map<ProjectType,Integer> contractPerType=user.getContractTypeNum();
        //用户的各类别标注质量的统计信息改变
        Map<ProjectType,Double> quality=user.getQuality();
        double oldTypeQuality=quality.get(type);
        double typeNum=contractPerType.get(type);
        double newTypeQuality=(oldTypeQuality*(typeNum-1)+gongXian)/typeNum;
        quality.put(type,newTypeQuality);
        user.setQuality(quality);
        //用户标注的综合质量发生改变
        double generalQuality=user.getGeneralQuality();
        int contractNum=user.getNum_Contract();
        double newGeneralQuality=(generalQuality*(contractNum-1)+gongXian)/contractNum;
        user.setGeneralQuality(newGeneralQuality);
        //用户的各等级贡献率改变
        Map<Integer,Integer> gongXianPhase=user.getGongXian();
        if(gongXian<=0.2){//0-20%
            gongXianPhase.put(0,gongXianPhase.get(0)+1);
        }else if(gongXian<=0.4){//20%-40%
            gongXianPhase.put(1,gongXianPhase.get(1)+1);
        }else if(gongXian<=0.6){//40%-60%
            gongXianPhase.put(2,gongXianPhase.get(2)+1);
        }else if(gongXian<=0.8){//60%-80%
            gongXianPhase.put(3,gongXianPhase.get(3)+1);
        }else {//80%-100%
            gongXianPhase.put(4,gongXianPhase.get(4)+1);
        }
        user.setGongXian(gongXianPhase);
        userDao.saveAndFlush(user);
        return ResultMessage.SUCCESS;
    }

    @Override
    public void updateAvgTimePerTime(String username, ProjectType type, Date startTime) {
        User user=userDao.searchUserById(username);
        Map<ProjectType,Double> avgTime=user.getAvgTimePerType();
        double timeThisType=avgTime.get(type);
        int contractThisType=user.getContractTypeNum().get(type);
        Date now=new Date();
        long time=(now.getTime()-startTime.getTime())/(1000*60);
        avgTime.put(type,(timeThisType*(contractThisType-1)+time)/contractThisType);
        user.setAvgTimePerType(avgTime);
        userDao.saveAndFlush(user);
    }

    @Override
    public ResultMessage NewRelease(String username, ProjectType type) {
        User user=userDao.getOne(username);
        user.setNum_Release(user.getNum_Release()+1);
        user.setActiveRelease(user.getActiveRelease()+ 10);

        Map<ProjectType,Integer> releaseTypeNum=user.getReleaseTypeNum();
        int old=releaseTypeNum.get(type);
        releaseTypeNum.put(type,old+1);
        user.setReleaseTypeNum(releaseTypeNum);
        userDao.saveAndFlush(user);
        return ResultMessage.SUCCESS;
    }

    @Override
    public ResultMessage NewContract(String username, ProjectType type) {
        User user=userDao.getOne(username);
        user.setNum_Contract(user.getNum_Contract()+1);
        user.setActiveContract(user.getActiveContract()+1);

        Map<ProjectType,Integer> contractTypeNum=user.getContractTypeNum();
        int old=contractTypeNum.get(type);
        contractTypeNum.put(type,old+1);
        user.setContractTypeNum(contractTypeNum);
        userDao.saveAndFlush(user);
        return ResultMessage.SUCCESS;
    }

    @Override
    public ArrayList<ActiveUser> getActiveRequester() {
        ArrayList<ActiveUser> activeUsers=new ArrayList<>();
        ArrayList<User> userArrayList=userDao.getActiveRequest();
        int count=0;
        for(User u:userArrayList){
            activeUsers.add(new ActiveUser(u.getUsername(),u.getDescription(),u.getGeneralQuality(),u.getActiveRelease()));
            count++;
            if(count>=8){
                break;
            }
        }
        return activeUsers;
    }

    @Override
    public ArrayList<ActiveUser> getActiveWorker() {
        ArrayList<ActiveUser> activeUsers=new ArrayList<>();
        ArrayList<User> userArrayList=userDao.getActiveWorker();
        int count=0;
        for(User u:userArrayList){
            activeUsers.add(new ActiveUser(u.getUsername(),u.getDescription(),u.getGeneralQuality(),u.getActiveContract()));
            count++;
            if(count>=8){
                break;
            }
        }
        return activeUsers;
    }

    @Override
    public ArrayList<String> getUserList() {
        return userDao.ListUserByRank(UserIdentity.COMMONUSER);
    }

    @Override
    public UserStatisticsToAdmin getUserStatisticsToAdmin(int year) {
        UserStatisticsToAdmin userStatisticsToAdmin=new UserStatisticsToAdmin();
        userStatisticsToAdmin.setTotalNum(userDao.countTotalUser());
        userStatisticsToAdmin.setOnlineNum(userDao.countOnlineUser());
        Calendar now=Calendar.getInstance();
        Date nowDate=new Date();
        //一周内的注册人数
        now.setTime(nowDate);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-7);
        int registerThisWeek=userDao.countRegisterInDays(now.getTime());
        userStatisticsToAdmin.setRegisterThisWeek(registerThisWeek);

        //一个月内的注册人数
        now.setTime(nowDate);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-30);
        int registerThisMonth=userDao.countRegisterInDays(now.getTime());
        userStatisticsToAdmin.setRegisterThisMonth(registerThisMonth);

        //每个月的注册人数
        ArrayList<ProjectStatistics> registerPerMonth=projectStatisticsDao.getRegisterPerMonth(year);
        Map<String,Integer> result=new HashMap<>();

        for(ProjectStatistics p:registerPerMonth){
            String m=p.getYearAndMonth().split("-")[1];
            result.put(m,p.getRegisterPerMonth());
        }
        userStatisticsToAdmin.setRegisterPerMonth(result);
        return userStatisticsToAdmin;
    }

}
