package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("cwzz")
public class PageController {
    @RequestMapping("introduce")
    public String introduce(){
        return "IntroducePage";
    }

    @RequestMapping("register")
    public String register(){
        return "RegisterPage";
    }

    @RequestMapping("login")
    public String login(){
        return "LoginPage";
    }

    @RequestMapping("home")
    public String home(){
        return "HomePage";
    }

    @RequestMapping("upload")
    public String upload(){
        return "upload";
    }

    @RequestMapping("information")
    public String information(){
        return "newinfo";
    }

    @RequestMapping("OhComfortable")
    public String formybabyMX(){
        return "IntegrityPage";
    }

    @RequestMapping("personal_center")
    public String personal_center(){
        return "PersonalCenterPage";
    }

    @RequestMapping("project_detail")
    public String project_detail(){
        return "ProjectDetailPage";
    }

    @RequestMapping("project_detail_for_publish")
    public String project_detail_for_publish(){
        return "ProjectDetailPageForPublish";
    }

    @RequestMapping("project_detail_for_adopt")
    public String project_detail_for_adopt(){
        return "ProjectDetailPageForAdopt";
    }

    @RequestMapping("work")
    public String work(){
        return "MainWorkingWeb";
    }

    @RequestMapping("search")
    public String search(){
        return "SearchPage";
    }

    @RequestMapping("test")
    public String test(){
        return "new_personal_info";
    }

    @RequestMapping("head")
    public String head(){
        return "head";
    }

    @RequestMapping("foot")
    public String foot(){
        return "foot";
    }

    @RequestMapping("info")
    public String info(){
        return "new_personal_info";
    }

    @RequestMapping("star")
    public String star(){
        return "star";
    }

    @RequestMapping("photos")
    public String photos(){
        return "PhotosPreview";
    }

    @RequestMapping("manager")
    public String manager(){
        return "ManagerPage_test";
    }

    @RequestMapping("personal_information")
    public String personal_information(){
        return "newinfo_personal_page";
    }

    @RequestMapping("manager_statistics")
    public String manager_statistics(){
        return "manager_statistics";
    }

    @RequestMapping("personal_statistics")
    public String personal_statistics(){
        return "personal_center_statistics";
    }

    @RequestMapping("manager_data")
    public String manager_data(){
        return "InformationPage";
    }

    @RequestMapping("integrity")
    public String integrity(){
        return "integrity_page";
    }

    @RequestMapping("integrity_result")
    public String integrity_result(){
        return "integrity_result";
    }

    @RequestMapping("integrity_result_title")
    public String integrity_result_title(){
        return "integrity_result_title";
    }

}
