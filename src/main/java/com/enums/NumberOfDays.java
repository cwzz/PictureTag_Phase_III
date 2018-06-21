package com.enums;

public enum NumberOfDays {
    All(-1,-1),
    Today(1,1),
    ThreeDays(1,3),
    OneWeek(0,7);

    private int min;
    private int max;
    NumberOfDays(int min, int max) {
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
