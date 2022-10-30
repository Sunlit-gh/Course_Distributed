package com.sunlit.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@lombok.Data
@AllArgsConstructor
@NoArgsConstructor
public class Data implements Serializable {
    Integer number;
    String str;
    ArrayList<Integer> arr;

    public Data(Integer number) {
        this.number = number;
    }

    public Data(String str) {
        this.str = str;
    }

    public Data(ArrayList<Integer> arr) {
        this.arr = arr;
    }
}
