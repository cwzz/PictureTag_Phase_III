package com.bl;

import com.blservice.LogBLService;
import com.dao.LogDao;
import com.model.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogBL implements LogBLService {

    private LogDao logDao;

    @Autowired
    public LogBL(LogDao logDao) {
        this.logDao = logDao;
    }


    @Override
    public void addLog(String username, String content) {
        Log newLog=new Log();
        newLog.setUsername(username);
        newLog.setContext(content);
        newLog.setDate(new Date());
        logDao.saveAndFlush(newLog);
    }

    @Override
    public List<Log> showLogList() {
        return logDao.findAll();
    }
}
