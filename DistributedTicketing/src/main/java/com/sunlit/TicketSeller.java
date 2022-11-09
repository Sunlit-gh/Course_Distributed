package com.sunlit;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class TicketSeller implements Runnable {
    //总票数
    private int ticket = 100;

    //Zookeeper服务器地址
    private static final String ZK_ADDRESS = "127.0.0.1:2181";

    //分布式可重入排它锁
    private final InterProcessMutex lock;

    public TicketSeller() {
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(3000, 10);

        //zookeeper创建链接
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString(ZK_ADDRESS)
                        .sessionTimeoutMs(60 * 1000)
                        .connectionTimeoutMs(15 * 1000)
                        .retryPolicy(retryPolicy)
                        .namespace("test")
                        .build();

        client.start();

        //创建锁
        lock = new InterProcessMutex(client, "/lock");
    }


    @Override
    public void run() {
        while (true) {
            try {
                //随机睡1-10秒，模拟业务不是一直持续，打乱排队顺序
                Thread.sleep(new Random().nextInt(10)*1000);
                System.out.println(Thread.currentThread().getName() + ":进入排队");
                //排队获取锁，等待10秒，超时则放弃排队，防止线程阻塞堆积
                lock.acquire(50, TimeUnit.SECONDS);
                if (ticket > 0) {
                    System.out.println(Thread.currentThread().getName() + ":进入临界区");
                    //模拟业务时长，形成队伍
                    Thread.sleep(new Random().nextInt(9)*1000);
                    ticket--;
                    System.out.println(Thread.currentThread().getName() + ":售出一张票，当前余票：" + ticket);
                }
            } catch (Exception e) {
                System.out.println("获取锁失败");
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
