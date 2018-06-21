package com.controller;

import com.enums.ResultMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MarkController {

    public static String locate;

    @RequestMapping(value = "/test_upload",method = RequestMethod.POST)
    public @ResponseBody
    ResultMessage register(@RequestBody String a){
        System.out.println(a);
        return ResultMessage.SUCCESS;
    }


    @RequestMapping("introduce")
    public String introduce(){
        return "LoginPage";
    }

    @RequestMapping("mark")
    public String mark(){
        return "MainWorkingWeb";
    }

    @RequestMapping("upload")
    public String upload(){
        return "upload";
    }

    @RequestMapping(value = "submit",method = RequestMethod.GET)
    public String submit(Model model, @RequestParam String content){
        System.out.println(content);
        locate=content;
        return "register";
    }

    @RequestMapping(value = "retrieve",method = RequestMethod.GET)
    public @ResponseBody String retrieve(Model model){
        return locate;
    }

    @RequestMapping(value = "firstpage")
    public String firstpage(){
        return "menu_search";
    }
}
