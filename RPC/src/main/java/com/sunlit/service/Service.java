package com.sunlit.service;

import java.util.ArrayList;

public interface Service {

    Integer Add(Integer a, Integer b);

    String StrCat(String a, String b);

    ArrayList<Integer> Sort(ArrayList<Integer> a, Integer n);
}
