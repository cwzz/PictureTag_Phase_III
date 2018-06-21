package com.vo.uservo;

import lombok.Data;

import java.util.ArrayList;

/**
 * @Author:zhangping
 * @Description:
 * @CreateData: 2018/4/21 15:28
 */
@Data
public class ContractProject {

    private String username;
    private int num_Contract;//用户承包的众包数量

    private ArrayList<ProBriefInfo> ContractAll;//用户所有承包的项目
    private ArrayList<ProBriefInfo> ContractOn;//用户承包的并且正在进行中的项目ID
    private ArrayList<ProBriefInfo> ContractExamine;//用户完成标注，提交等待验收
    private ArrayList<ProBriefInfo> ContractAbort;//工人选择后，中途放弃的
    private ArrayList<ProBriefInfo> ContractFinished;//用户承包的并且工作结束

    public ContractProject(){}
    public ContractProject(String username, int num, ArrayList<ProBriefInfo> all, ArrayList<ProBriefInfo> on,
                           ArrayList<ProBriefInfo> examine, ArrayList<ProBriefInfo> abort, ArrayList<ProBriefInfo> finish){
        this.username=username;
        this.num_Contract=num;
        this.ContractAll=all;
        this.ContractOn=on;
        this.ContractExamine=examine;
        this.ContractAbort=abort;
        this.ContractFinished=finish;
    }
}
