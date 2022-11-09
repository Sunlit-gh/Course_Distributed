package com.sunlit;

public class TicketTest
{
    public static void main(String[] args)
    {
        TicketSeller ticketSeller = new TicketSeller();

        //模拟5个售票窗口
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(ticketSeller, "窗口" + i);
            thread.start();
        }
    }
}
