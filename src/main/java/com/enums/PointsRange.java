package com.enums;

import lombok.Data;

public enum PointsRange {
    BelowThirty(0,30),
    BetweenThirtyAndFifty(30,50),
    BetweenFiftyAndOneHundred(50,100),
    AboveOneHundred(100,10000),
    All(0,10000);

    private int min;
    private int max;

    PointsRange(int min,int max) {
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
