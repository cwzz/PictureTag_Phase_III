package com.controller;

import com.bl.integratebl.test;
import com.blservice.PersonalTagBLService;
import com.enums.ResultMessage;
import com.vo.personaltagvo.ShowReq;
import com.vo.personaltagvo.UpdatePersonalTagReq;
import com.vo.tag.PersonalTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/personalTag")
public class PersonalTagController {
    private final PersonalTagBLService personalTagBLService;

    private final test t;

    @Autowired
    public PersonalTagController(PersonalTagBLService personalTagBLService, test t) {
        this.personalTagBLService = personalTagBLService;
        this.t = t;
    }

    @RequestMapping(value = "/test")
    public @ResponseBody
    ResultMessage test(){
        t.go3();
        return ResultMessage.SUCCESS;
    }

    @RequestMapping(value = "/test1")
    public @ResponseBody
    ArrayList<PersonalTagVO> test1(){
        return personalTagBLService.getAllPersonalTagByPid("20180614112314-jane");
    }

    @RequestMapping(value = "/update")
    public @ResponseBody
    ResultMessage updatePersonalTag(@RequestBody UpdatePersonalTagReq updatePersonalTagReq){
        return personalTagBLService.updatePersonalTag(updatePersonalTagReq.getPid(),updatePersonalTagReq.getUid(),updatePersonalTagReq.getPictures());
    }

    @RequestMapping(value = "/showPersonalTag")
    public @ResponseBody
    PersonalTagVO showPersonalTagBySomeOne(@RequestBody ShowReq showReq){
        return personalTagBLService.showPersonalTagBySomeOne(showReq.getPid(),showReq.getUid());
    }

    @RequestMapping(value="/show")
    public @ResponseBody
    ArrayList<PersonalTagVO> showPersonalTags(@RequestParam String pid){
        return personalTagBLService.getAllPersonalTagByPid(pid);
    }

    @RequestMapping(value = "/getNext")
    public @ResponseBody
    String[] getNextGroup(@RequestParam String username,@RequestParam String projectID){
        return personalTagBLService.getNextGroupPicture(projectID,username);
    }

    @RequestMapping(value = "/requestViewAllWork")
    public @ResponseBody
    List<String> getHisWork(@RequestParam String username, @RequestParam String projectID){
        return personalTagBLService.requesterCheckAllWork(username, projectID);
    }
}
