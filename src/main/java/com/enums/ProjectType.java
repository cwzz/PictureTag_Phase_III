package com.enums;

public enum ProjectType {
    ANIMALTAG("动物类"),//动物
    SCENETAG("风景类"),//风景
    PERSONTAG("人物类"),//人物
    GOODSTAG("物品类"),//物品
    OTHERSTAG("其他类"),//其他
    All("全部类");

    private String chinese;

    ProjectType(String chinese){
        this.chinese=chinese;
    }

    public String getChinese() {
        return chinese;
    }
}
