package com.sunlit.service;

import com.sunlit.common.Data;

import java.util.ArrayList;
import java.util.Collections;

public class ServiceImpl implements Service {

    public Integer Add(Integer a, Integer b) {
        return a + b;
    }

    public String StrCat(String a, String b) {
        return a + b;
    }


    public ArrayList<Integer> Sort(ArrayList<Integer> a, Integer n) {
        Collections.sort(a);
        return a;
    }
}
