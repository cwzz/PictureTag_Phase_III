package com.controller;

import com.bl.integratebl.AnalyseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@Controller
@RequestMapping("/integrity")
public class IntegrityController {
    @Autowired
    private AnalyseData analyseData;

    @RequestMapping(value = "integrity")
    public @ResponseBody void integrity(@RequestParam String pid){
        analyseData.integrityProject(pid);
    }

    @RequestMapping(value = "download")
    public @ResponseBody void download(@RequestBody ArrayList<String> urls){
        analyseData.downloadPictures(urls);
    }
}
