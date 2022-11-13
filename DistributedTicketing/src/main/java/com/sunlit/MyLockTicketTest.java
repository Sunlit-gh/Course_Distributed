package com.sunlit;

public class MyLockTicketTest {
    public static void main(String[] args) {

        MyLockTicketSeller ticketSeller = new MyLockTicketSeller();

        //模拟5个售票窗口
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(ticketSeller, "窗口" + i);
            thread.start();
        }
    }
}
