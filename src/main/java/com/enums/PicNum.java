package com.enums;

public enum PicNum {
    UNDER_500(1,500),
    FiveToTen(500,1000),
    TenToTwenty(1000,2000),
    TwentyToThirty(2000,3000),
    AboveThirty(3000,100000);

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
