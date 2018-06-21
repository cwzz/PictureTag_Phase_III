package com.bl.project;

import com.enums.ProjectState;
import com.model.Project;
import com.util.TransSetToArray;
import com.vo.projectvo.ProjectBasic;
import com.vo.projectvo.ProjectVO;
import com.vo.projectvo.UploadProVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProjectTransVOPO {

    private final TransSetToArray transSetToArray;

    @Autowired
    public ProjectTransVOPO(TransSetToArray transSetToArray) {
        this.transSetToArray = transSetToArray;
    }

    ProjectVO transProjectToProjectVO(Project project){
        return new ProjectVO(project.getPro_ID(),project.getPro_name(),project.getPoints(),project.getBrief_intro(),
                transSetToArray.transSetToStringArray(project.getWorkerList()),project.getReleaseTime(), project.getDeadLine(),
                project.getRemainTime(),project.getPro_type(),project.getPro_state(), project.getPro_requester(),
                project.getDetailRequire(),project.getNote(),transSetToArray.transListTOArray(project.getUrls()),
                transSetToArray.transSetToStringArray(project.getFinished_list()));
    }

    ProjectBasic transProjectToProjectBasic(Project project){
        return new ProjectBasic(project.getPro_ID(),project.getPro_name(),project.getPoints(),project.getBrief_intro(),
                project.getReleaseTime(),project.getRemainTime(),project.getPro_type(),project.getPro_state(),project.getPro_requester(),
                project.getUrls().size(),project.getWorkerList().size());
    }

    //我觉得不需要
//    public Project transProjectVOToProject(ProjectVO projectVO){
//        Set<String> urlsSet=new HashSet<>(Arrays.asList(projectVO.getUrls()));
//        Set<String> workerListSet=new HashSet<>(Arrays.asList(projectVO.getWorkerList()));
//        Set<String> finishedSet=new HashSet<>(Arrays.asList(projectVO.getFinished_list()));
//        return new Project(projectVO.getPro_ID(),projectVO.getPro_name(),projectVO.getPoints(),projectVO.getBrief_intro(),workerListSet,projectVO.getReleaseTime(),projectVO.getDeadLine(),projectVO.getRemainTime(),projectVO.getPro_type(),projectVO.getPro_state(),projectVO.getPro_requester(),projectVO.getDetailRequire(),projectVO.getNote(),urlsSet,finishedSet);
//    }

    Project transUploadProVOToProject(UploadProVO uploadProVO){
        List<String> urls=Arrays.asList(uploadProVO.getUrls());
        Set<String> workersSet=new HashSet<>();
        Set<String> finishedSet=new HashSet<>();
        List<String> combineRes_urls=new ArrayList<>();
        return new Project(uploadProVO.getPro_ID(),uploadProVO.getPro_name(),uploadProVO.getPoints(),uploadProVO.getBrief_intro(),workersSet,
                null,uploadProVO.getDeadLine(),uploadProVO.getRemainTime(),uploadProVO.getPro_type(), ProjectState.DRAFT,
                uploadProVO.getPro_requester(),uploadProVO.getDetailRequire(),uploadProVO.getNote(),urls,finishedSet,0,0,combineRes_urls);
    }

}

