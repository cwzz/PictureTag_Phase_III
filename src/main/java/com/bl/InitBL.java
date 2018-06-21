package com.bl;

import com.dao.ProjectDao;
import com.dao.ProjectStatisticsDao;
import com.model.ProjectStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class InitBL implements ApplicationRunner{
    @Autowired
    private ProjectStatisticsDao projectStatisticsDao;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");//设置日期格式
        String currentTime=df.format(new Date());
        if(!projectStatisticsDao.existsById(currentTime)){
            ProjectStatistics projectStatistics=new ProjectStatistics(currentTime);
            projectStatisticsDao.saveAndFlush(projectStatistics);
        }

    }
}
