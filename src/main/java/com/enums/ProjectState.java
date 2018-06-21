package com.enums;

/**
 * @Author:zhangping
 * @Description: 发布的项目的状态
 * @CreateData: 2018/4/7 16:01
 */
public enum ProjectState {
    All(-1,"所有"),
    RECYCLE(0,"回收"),//回收站
    DRAFT(1,"草稿"),//草稿
    REALEASED(2,"已发布"),//已发布
    TAGING(3,"标注"),//正在标注
    SUBMITTED(4,"提交"),
    EXAMINE(5,"检查中"),
    GIVEUP(6,"放弃中"),//工人放弃项目
    FINISHED(7,"已结束");//项目结束

    private int num;
    private String chinese;
    ProjectState(int num,String chinese) {
        this.num=num;
        this.chinese=chinese;
    }

    public int getNum() {
        return num;
    }

    public String getChinese(){
        return this.chinese;
    }
}
