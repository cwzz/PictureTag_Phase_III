package com.enums;

public enum PicNum {
    UNDER_100(1,100),
    OneToFive(100,500),
    FiveToTen(500,1000),
    TenToFifty(1000,5000),
    AboveFifty(5000,10000);

    private int min;
    private int max;

    PicNum(int min, int max) {
        this.min=min;
        this.max=max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }


}
