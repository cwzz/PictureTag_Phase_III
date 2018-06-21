package com.blservice;

import com.model.Log;

import java.util.List;

public interface LogBLService {

    void addLog(String username, String content);

    List<Log> showLogList();
}
