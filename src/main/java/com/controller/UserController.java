package com.controller;


import com.blservice.UserBLService;
import com.enums.ResultMessage;
import com.model.BrowseRecord;
import com.vo.uservo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Set;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserBLService userBLService;
    @Autowired
    public UserController(UserBLService userBLService) {
        this.userBLService = userBLService;
    }

    @RequestMapping(value = "/register")
    public @ResponseBody
    ResultMessage register(@RequestBody NewUser newUser){
        return userBLService.register(newUser);
    }

    //用户修改自己的个人信息，可以修改的只有邮箱，密码，标签，自我描述
    @RequestMapping(value = "/reset")
    public @ResponseBody
    ResultMessage resetUserPersonal(@RequestBody PersonalCenter user){
        return userBLService.reset(user);
    }

    @RequestMapping(value = "/login")
    public @ResponseBody
    ResultMessage login(@RequestBody LoginReq loginReq){
        return userBLService.login(loginReq);
    }

    @RequestMapping(value = "/logout")
    public @ResponseBody
    ResultMessage logout(@RequestParam String username){
        return userBLService.logout(username);
    }

    @RequestMapping(value = "resetPass")
    public @ResponseBody
    ResultMessage resetPassword(@RequestParam String username,@RequestParam String oldPass,@RequestParam String newPass){
        return userBLService.resetPassword(username,oldPass,newPass);
    }
    @RequestMapping(value = "/Personal")
    public @ResponseBody
    PersonalCenter search1(@RequestParam String username){
        return userBLService.getPersonal(username);
    }

    @RequestMapping(value = "/Statistics")
    public @ResponseBody
    UserStatistics search2(@RequestParam String username){
        return userBLService.getUserStatistics(username);
    }

    @RequestMapping(value = "/MyRelease")
    public @ResponseBody
    ReleaseProject search3(@RequestParam String username){
        return userBLService.getRelease(username);
    }

    @RequestMapping(value = "/MyContract")
    public @ResponseBody
    ContractProject search4(@RequestParam String username){
        return userBLService.getContract(username);
    }

    @RequestMapping(value = "/browse")
    public @ResponseBody
    void browseProject(@RequestBody NewBrowseRecord recordVO){
        userBLService.insertBrowseRecord(recordVO);
    }

    @RequestMapping(value = "getBrowseRecord")
    public @ResponseBody
    Set<BrowseRecord> getBrowseList(@RequestParam String username){
        return userBLService.getBrowseRecord(username);
    }

    //根据经验值降序得到用户名
    @RequestMapping(value = "listUser")
    public @ResponseBody
    ArrayList<String> test(){
        return userBLService.getUserList();
    }

    //得到活跃发包方的列表
    @RequestMapping(value = "/activeRequester")
    public @ResponseBody
    ArrayList<ActiveUser> getActiveRequester(){
        return userBLService.getActiveRequester();
    }
    //得到活跃承包方的列表
    @RequestMapping(value = "/activeWorker")
    public @ResponseBody
    ArrayList<ActiveUser> getActiveWorker(){
        return userBLService.getActiveWorker();
    }

    @RequestMapping(value = "/UserToAdmin")
    public @ResponseBody
    UserStatisticsToAdmin getUserStatisticsToAdmin(@RequestParam int year){
        return userBLService.getUserStatisticsToAdmin(year);
    }

}
