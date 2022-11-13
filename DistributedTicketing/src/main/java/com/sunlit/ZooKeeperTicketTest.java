package com.sunlit;

public class ZooKeeperTicketTest
{
    public static void main(String[] args)
    {
        ZookeeperTicketSeller ticketSeller = new ZookeeperTicketSeller();

        //模拟5个售票窗口
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(ticketSeller, "窗口" + i);
            thread.start();
        }
    }
}
