package com.sunlit;

public class MyLockTicketSeller implements Runnable {
    //总票数
    private int ticket = 10;

    private final MyLock lock = new MyLock();

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + ":进入排队");
                lock.acquire();
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + ":进入临界区");
                    ticket--;
                    System.out.println(Thread.currentThread().getName() + ":售出一张票，当前余票：" + ticket);
                }else {
                    System.out.println(Thread.currentThread().getName() + ":票已售罄");
                    System.exit(1);
                    break;
                }
            } catch (Exception e) {
                System.out.println(Thread.currentThread().getName() + ":获取锁失败");
                e.printStackTrace();
                System.exit(1);
            } finally {
                try {
                    System.out.println(Thread.currentThread().getName() + ":退出临界区");

                    //释放锁
                    lock.release();

                } catch (Exception e) {
                    System.out.println(Thread.currentThread().getName() + "放弃排队");
                    //e.printStackTrace();
                }
            }
        }
    }
}
