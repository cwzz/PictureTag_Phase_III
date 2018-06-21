package com.controller;

import com.blservice.LogBLService;
import com.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/log")
public class LogController {

    @Autowired
    private LogBLService logBLService;

    @RequestMapping(value="/showList")
    public @ResponseBody
    List<Log> listAllLog(){
        return logBLService.showLogList();
    }

}
