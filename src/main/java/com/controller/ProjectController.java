package com.controller;


import com.blservice.ProjectBLService;
import com.dao.ProjectStatisticsDao;
import com.enums.PicNum;
import com.enums.ResultMessage;
import com.enums.SearchReq;
import com.util.oss.OSSClientUtil;
import com.vo.personaltagvo.CombineResVO;
import com.vo.projectvo.*;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectBLService projectBLService;
    private final OSSClientUtil ossClientUtil;

    @Autowired
    private ProjectStatisticsDao projectStatisticsDao;

    @Autowired
    public ProjectController(ProjectBLService projectBLService, OSSClientUtil ossClientUtil) {
        this.projectBLService = projectBLService;
        this.ossClientUtil = ossClientUtil;

    }

    @RequestMapping(value = "/finish")
    public @ResponseBody
    ResultMessage finish(@RequestParam String pid){
        return projectBLService.changeToFinish(pid);
    }

    @RequestMapping(value = "/changeBaseToUrl")
    public @ResponseBody String test(@RequestParam String base64, @RequestParam String filename, @RequestParam String projectID){
        return ossClientUtil.uploadPicture(projectID,filename+".jpg",base64);
    }

    @RequestMapping(value = "/uploadZip")
    public @ResponseBody
    ArrayList<String> uploadZip(MultipartFile zipFile, String projectID){
        return ossClientUtil.uploadZip(zipFile,projectID);
    }

    //给发起者推荐合适的积分范围
    @RequestMapping(value = "/predictPrice")
    public @ResponseBody
    double recommendCredits(@RequestParam int pictureNum){
        return projectBLService.predictPrice(pictureNum);
    }

    //发起者追加积分
    @RequestMapping(value = "/addToCredits")
    public @ResponseBody
    ResultMessage addToCredits(@RequestParam String username, @RequestParam String projectID, @RequestParam double credits){
        return projectBLService.addCredits(username,projectID,credits);
    }

    @RequestMapping(value = "/showList",method = RequestMethod.GET)
    public @ResponseBody
    JSONArray showProList(){
        return projectBLService.getProjectListForCommonUser();
    }
//
    @RequestMapping(value="/add",method=RequestMethod.POST)
    public @ResponseBody String add(@RequestParam String username)
    {
        return projectBLService.addPro(username);
    }
//
    @RequestMapping(value="/save",method=RequestMethod.POST)
    public @ResponseBody
    ResultMessage save(@RequestBody UploadProVO uploadProVO){
        return projectBLService.savePro(uploadProVO);
    }
//
//    /**
//     * @Author: Jane
//     * @Description: uplode project
//     * @Date: 10:55 2018/4/11
//     */
//
    @RequestMapping(value="/upload",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage upload(@RequestBody UploadProVO uploadProVO){
//        System.out.println(uploadProVO.getPro_ID());
//        System.out.println(uploadProVO.toString());
        return projectBLService.uploadPro(uploadProVO);
    }

    /**
     * @Author: Jane
     * @Description: remove project
     * @Date: 17:35 2018/4/14
     */
    @RequestMapping(value="/remove",method=RequestMethod.POST)
    public @ResponseBody
    ResultMessage removePro(@RequestBody ProReq proReq){
        return projectBLService.removePro(proReq.getUsername(), proReq.getPid());
    }

    /**
     * @Author: Jane
     * @Description: delete project
     * @Date: 17:35 2018/4/14
     */
    @RequestMapping(value="/delete",method=RequestMethod.POST)
    public @ResponseBody
    ResultMessage deletePro(@RequestBody ProReq proReq){
        return projectBLService.delPro(proReq.getUsername(), proReq.getPid());
    }

    @RequestMapping(value="/recover",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage recoverPro(@RequestBody ProReq proReq){
        return projectBLService.recoverPro(proReq.getUsername(), proReq.getPid());
    }

    /**
     * @Author: Jane
     * @Description: read project
     * @Date: 23:41 2018/4/14
     */
    @RequestMapping(value = "/read",method = RequestMethod.POST)
    public @ResponseBody
    ProjectVO readProject(@RequestParam String pid){
        ProjectVO p=projectBLService.viewPro(pid);
        //System.err.println("Aaaaaaaaaaaaaaaaaaaa"+p.getReleaseTime());
        if(p==null){
            System.out.println("null");
        }
        return p;
    }

    @RequestMapping(value="/choosePro",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage sendRequest(@RequestBody ProReq proReq){
        return projectBLService.choosePro(proReq.getUsername(),proReq.getPid());
    }

//    @RequestMapping(value = "/search",method = RequestMethod.POST)
//    public  @ResponseBody
//    ArrayList<Project> search(@RequestParam String keywords){
//        return projectDao.searchPro(keywords);
//    }

//
    @RequestMapping(value = "/search")
    public @ResponseBody ArrayList<ProjectBasic> searchPro(@RequestBody SearchReq searchReq){
//        SearchReq searchReq1=new SearchReq();
//        searchReq1.setKeywords("猫，狗,动物 分类。.");
//        searchReq1.setNumberOfDays(NumberOfDays.All);
//        searchReq1.setPointsRange(PointsRange.All);
//        searchReq1.setState(SearchProState.All);
//        searchReq1.setType(ProjectType.All);
//        return projectBLService.searchPro(searchReq1.getKeywords(),searchReq1.getType(),searchReq1.getState(),searchReq1.getPointsRange(),searchReq1.getNumberOfDays());
        return projectBLService.searchPro(searchReq.getKeywords(),searchReq.getType(),searchReq.getState(),searchReq.getPointsRange(),searchReq.getNumberOfDays());
//         return projectBLService.getProjectListForCommonUser();
    }

//    @RequestMapping(value = "/search1")
//    public @ResponseBody int searchPro1(){
//        return projectDao.countNameNum("动物");
//    }
//
//    @RequestMapping(value = "/searchInUser")
//    public @ResponseBody
//    ArrayList<ProIDName> searchPro(@RequestBody SearchInUserReq searchInUserReq){
//        System.out.println(searchInUserReq.toString());
//        return projectBLService.searchProInUser(searchInUserReq.getKeywords(),searchInUserReq.getSearchObject());
//    }
//

    @RequestMapping(value = "/quit",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage quit(@RequestBody ProReq proReq){
        return projectBLService.quitPro(proReq.getUsername(),proReq.getPid());
    }


    @RequestMapping(value="/submit",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage submit(@RequestBody ProReq proReq){
        return projectBLService.submitPro(proReq.getUsername(),proReq.getPid());
    }

    @RequestMapping(value = "/saveCombineRes",method = RequestMethod.POST)
    public @ResponseBody ResultMessage saveCombineRes(@RequestBody CombineResVO combineResVO){
        return projectBLService.saveCombineRes(combineResVO);
    }

    @RequestMapping(value = "/showCombineRes",method = RequestMethod.POST)
    public @ResponseBody
    List<String> showCombineRes(@RequestParam String pid){
        return projectBLService.showCombineRes(pid);
    }


    @RequestMapping(value = "/totalFinishedNum",method = RequestMethod.POST)
    public @ResponseBody
    int totalFinishedNum(){
        return projectBLService.totalFinishedNum();
    }

    @RequestMapping(value = "/totalReleasedNum",method = RequestMethod.POST)
    public @ResponseBody
    int totalReleasedNum(){
        return projectBLService.totalReleasedNum();
    }

//    @RequestMapping(value = "/releasedPerMonth",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedPerMonth(@RequestParam String year){
//        return projectBLService.releasedPerMonth(year);
//    }
//
//    @RequestMapping(value = "/waitUndertakePerMonth",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUndertakePerMonth(@RequestParam String year){
//        return projectBLService.waitUndertakePerMonth(year);
//    }
//
//    @RequestMapping(value = "/finishedPerMonth",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> finishedPerMonth(@RequestParam String year){
//        return projectBLService.finishedPerMonth(year);
//    }
//
//    @RequestMapping(value = "/releasedAnimalNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedAnimalNum(@RequestParam String year){
//        return projectBLService.releasedAnimalNum(year);
//    }
//
//    @RequestMapping(value = "/releasedSceneNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedSceneNum(@RequestParam String year){
//        return projectBLService.releasedSceneNum(year);
//    }
//
//    @RequestMapping(value = "/releasedPersonNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedPersonNum(@RequestParam String year){
//        return projectBLService.releasedPersonNum(year);
//    }
//
//    @RequestMapping(value = "/releasedGoodsNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedGoodsNum(@RequestParam String year){
//        return projectBLService.releasedGoodsNum(year);
//    }
//
//    @RequestMapping(value = "/releasedOthersNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> releasedOthersNum(@RequestParam String year){
//        return projectBLService.releasedOthersNum(year);
//    }
//
//    @RequestMapping(value = "/waitUnderTakeAnimalNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUnderTakeAnimalNum(@RequestParam String year){
//        return projectBLService.waitUndertakeAnimalNum(year);
//    }
//
//    @RequestMapping(value = "/waitUnderTakeSceneNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUnderTakeSceneNum(@RequestParam String year){
//        return projectBLService.waitUndertakeSceneNum(year);
//    }
//
//    @RequestMapping(value = "/waitUnderTakePersonNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUnderTakePersonNum(@RequestParam String year){
//        return projectBLService.waitUndertakePersonNum(year);
//    }
//
//    @RequestMapping(value = "/waitUnderTakeGoodsNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUnderTakeGoodNum(@RequestParam String year){
//        return projectBLService.waitUndertakeGoodsNum(year);
//    }
//
//    @RequestMapping(value = "/waitUnderTakeOthersNum",method = RequestMethod.POST)
//    public @ResponseBody
//    Map<String,Integer> waitUnderTakeOthersNum(@RequestParam String year){
//        return projectBLService.waitUndertakeOthersNum(year);
//    }

//    @RequestMapping(value = "/test2",method = RequestMethod.POST)
//    public @ResponseBody double test3(){
//        return projectStatisticsDao.calAvgReleasedAnimalNum("2018");
//    }
    @RequestMapping(value = "/statistics",method = RequestMethod.POST)
    public @ResponseBody
    Statistics Statistics(@RequestParam String year){
        Statistics statistics=new Statistics();
        statistics.setAvgReleasedNum(projectBLService.avgReleasedNum(year));

        statistics.setFinishedAnimalNum(projectBLService.finishedAnimalNum(year));
        statistics.setFinishedGoodsNum(projectBLService.finishedGoodsNum(year));
        statistics.setFinishedOthersNum(projectBLService.finishedOthersNum(year));
        statistics.setFinishedPersonNum(projectBLService.finishedPersonNum(year));
        statistics.setFinishedSceneNum(projectBLService.finishedSceneNum(year));

        statistics.setWaitUndertakeAnimalNum(projectBLService.waitUndertakeAnimalNum(year));
        statistics.setWaitUndertakeGoodsNum(projectBLService.waitUndertakeGoodsNum(year));
        statistics.setWaitUndertakeOthersNum(projectBLService.waitUndertakeOthersNum(year));
        statistics.setWaitUndertakeSceneNum(projectBLService.waitUndertakeSceneNum(year));
        statistics.setWaitUndertakePersonNum(projectBLService.waitUndertakePersonNum(year));

        statistics.setReleasedAnimalNum(projectBLService.releasedAnimalNum(year));
        statistics.setReleasedGoodsNum(projectBLService.releasedGoodsNum(year));
        statistics.setReleasedOthersNum(projectBLService.releasedOthersNum(year));
        statistics.setReleasedPersonNum(projectBLService.releasedPersonNum(year));
        statistics.setReleasedSceneNum(projectBLService.releasedSceneNum(year));

        statistics.setReleasedPerMonth(projectBLService.releasedPerMonth(year));
        statistics.setFinishedPerMonth(projectBLService.finishedPerMonth(year));
        statistics.setWaitUndertakePerMonth(projectBLService.waitUndertakePerMonth(year));

        return statistics;

    }

    @RequestMapping(value = "/recToUser",method = RequestMethod.POST)
    public @ResponseBody ArrayList<Recommend1> recToUser(@RequestParam String uid){
        return projectBLService.recommendPro(uid);
    }

    @RequestMapping(value = "/recSimiPro",method= RequestMethod.POST)
    public @ResponseBody ArrayList<Recommend2> recSimiPro(@RequestParam String pid,@RequestParam String uid){
        return projectBLService.recommendSimiPro(pid,uid);
    }

    @RequestMapping(value = "/showFinishCondition",method = RequestMethod.POST)
    public @ResponseBody ArrayList<FinishCondition> showFinishCondition(@RequestParam String pid){
        return projectBLService.showFinishConditionList(pid);
    }

    @RequestMapping(value = "/getNewest",method = RequestMethod.GET)
    public @ResponseBody ArrayList<Recommend1> getNewest(){
        return projectBLService.newestPro();
    }

    @RequestMapping(value = "/mark",method = RequestMethod.POST)
    public @ResponseBody ResultMessage mark(@RequestParam String pid,@RequestParam int score){
        return projectBLService.markCombineRes(pid,score);
    }

}
