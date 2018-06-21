package com.enums;

public enum SearchProState {
    All(-1),
    BidTime(0),
    Underway(1),
    Finished(2);

    private int num;

    SearchProState(int i) {
        this.num=i;
    }

    public int getNum() {
        return num;
    }
}
