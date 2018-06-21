package com.util;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TransSetToArray {
    public String[] transSetToStringArray(Set<String> a){
        String[] arr=new String[a.size()];
        arr=a.toArray(arr);
        return arr;
    }

    public String[] transListTOArray(List<String> list){
        String[] strings = new String[list.size()];
        strings=list.toArray(strings);
        return strings;
    }
}
