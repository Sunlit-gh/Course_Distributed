package com.sunlit;

import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("请输入线程数量：");
        Scanner sc = new Scanner(System.in);
        int processes = sc.nextInt();

        MyThread[] t = new MyThread[processes];
        Random r = new Random();
        for (int i = 0; i < processes; i++) {
            t[i] = new MyThread(new Process(r.nextInt(100)), processes);
        }
        Election.initialElection(t);
        for (int i = 0; i < processes; i++) {
            new Thread(t[i]).start();
        }
    }
}
