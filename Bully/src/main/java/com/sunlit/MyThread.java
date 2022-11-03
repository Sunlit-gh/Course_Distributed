package com.sunlit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class MyThread implements Runnable {

    private Process process;

    private final int total_processes;

    private static boolean[] messageFlag;

    ServerSocket[] sock;

    int pos;


    Random r;

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public static boolean isMessageFlag(int index) {
        return MyThread.messageFlag[index];
    }

    public static void setMessageFlag(boolean messageFlag, int index) {
        MyThread.messageFlag[index] = messageFlag;
    }

    public MyThread(Process process, int total_processes) {
        this.process = process;
        this.total_processes = total_processes;
        r = new Random();
        MyThread.messageFlag = new boolean[total_processes];
        for (int i = 0; i < total_processes; i++) {
            MyThread.messageFlag[i] = false;
        }
        this.sock = new ServerSocket[total_processes];
    }


    synchronized private void pingCoOrdinator() {
        try {
            Election.pingLock.lock();
            if (Election.isPingFlag()) {
                System.out.println("P" + this.process.getPid() + ": 管理者是否存活?");
                Socket outgoing = new Socket("127.0.0.1", 12345);
                outgoing.close();
            }
        } catch (Exception ex) {
            Election.setPingFlag(false);
            Election.setElectionFlag(true);
            Election.setElectionDetector(this.process);

            System.out.println("P" + this.process.getPid() + ": 管理者挂掉了, 我来开始选举...");
        } finally {
            Election.pingLock.unlock();
        }
    }


    //管理员
    synchronized private void serve() {
        try {
            boolean done = false;
            Socket incoming = null;
            ServerSocket s = new ServerSocket(12345);
            Election.setPingFlag(true);

            //设置一个管理员随机存活时间，时间结束后睡眠
            int temp = this.r.nextInt(5) + 5;
            for (int counter = 0; counter < temp; counter++) {
                //如果有线程ping了管理员，那么就回复
                incoming = s.accept();
                if (Election.isPingFlag())
                    System.out.println("P" + this.process.getPid() + ": 是的");
                //如果有线程恢复了，则让位
                Scanner scan = new Scanner(incoming.getInputStream());
                PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
                while (scan.hasNextLine() && !done) {
                    String line = scan.nextLine();
                    if (line.equals("谁是管理者?")) {
                        System.out.println("P" + this.process.getPid() + ": 我");
                        out.println(this.process.getPid());
                        out.flush();
                    } else if (line.equals("卸任")) {
                        this.process.setCoordinator(false);
                        out.println("成功卸任");
                        out.flush();
                        incoming.close();
                        s.close();
                        System.out.println("P" + this.process.getPid() + ": 哦，好吧");
                        return;

                    } else if (line.equals("不要卸任")) {
                        done = true;
                    }
                }
            }

            this.process.setCoordinator(false);
            this.process.setDown(true);
            try {
                incoming.close();
                s.close();
                sock[pos].close();
                //管理员挂掉的时间
                Thread.sleep(15000);
                //恢复线程
                recovery();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }


    private void executeJob() {

        int temp = r.nextInt(10);
        for (int i = 0; i <= temp; i++) {
            try {
                Thread.sleep((temp + 1) * 100);
            } catch (InterruptedException e) {
                System.out.println("Error Executing Thread:" + process.getPid());
                System.out.println(e.getMessage());
            }
        }
    }

    @SuppressWarnings({"static-access"})
    synchronized private boolean sendMessage() {
        boolean response = false;
        try {
            Election.electionLock.lock();
            //向比自己pid大的线程发送选举信息
            if (Election.isElectionFlag() && !MyThread.isMessageFlag(pos)
                    && this.process.pid >= Election.getElectionDetector().getPid()) {
                for (int i = pos + 1; i < this.total_processes; i++) {
                    try {
                        Socket electionMessage = new Socket("127.0.0.1", 10000 + i);
                        System.out.println("P" + Election.processIds[i] + ": 我在");
                        electionMessage.close();
                        response = true;
                    } catch (IOException ex) {
                        System.out.println("P" + this.process.getPid() + ": P"
                                + Election.processIds[i]
                                + " 没有回复");
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                this.setMessageFlag(true, pos);
                Election.electionLock.unlock();
                return response;
            } else {
                throw new Exception();
            }
        } catch (Exception ex1) {
            Election.electionLock.unlock();
            return true;
        }
    }

    //恢复线程
    synchronized private void recovery() {
        while (Election.isElectionFlag()) {
            // wait;
        }
        System.out.println("P" + this.process.getPid() + ": 我恢复了!");
        try {
            Election.pingLock.lock();
            Election.setPingFlag(false);
            //先当前管理员发送信息并霸道占位
            Socket outgoing = new Socket("127.0.0.1", 12345);
            Scanner scan = new Scanner(outgoing.getInputStream());
            PrintWriter out = new PrintWriter(outgoing.getOutputStream(), true);
            System.out.println("P" + this.process.getPid() + ": 谁是管理者?");
            out.println("谁是管理者?");
            out.flush();
            String pid = scan.nextLine();
            if (this.process.getPid() > Integer.parseInt(pid)) {
                out.println("卸任");
                out.flush();
                System.out.println("P" + this.process.getPid() + ": " + pid + "你卸任吧！");
                String resignStatus = scan.nextLine();
                if (resignStatus.equals("成功卸任")) {
                    this.process.setCoordinator(true);
                    sock[pos] = new ServerSocket(10000 + pos);
                    System.out.println("P" + this.process.getPid() + ": P" + pid +
                            "已经卸任, 现在我是管理员!");
                }
            } else {
                out.println("不要卸任");
                out.flush();
            }
            Election.pingLock.unlock();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }


    @Override
    public void run() {
        //获取本进程在进程中的位置，方便后续发送消息的操作
        for (int i = 0; i < Election.processIds.length; i++) {
            if (Election.processIds[i] == this.process.getPid()) {
                pos = i;
                break;
            }
        }

        //创建每个进程的socket
        try {
            sock[pos] = new ServerSocket(10000 + pos);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        while (true) {
            //如果是管理员
            if (process.isCoordinator()) {
                serve();
            } else {
                //如果不是管理员
                while (true) {
                    //随机等待一段时间
                    executeJob();
                    //ping管理员
                    pingCoOrdinator();
                    //如果开始选举
                    if (Election.isElectionFlag()) {
                        //如果选举成功
                        if (!sendMessage()) {
                            Election.setElectionFlag(false);
                            System.out.println("新的管理者: P" + this.process.getPid());
                            this.process.setCoordinator(true);
                            for (int i = 0; i < total_processes; i++) {
                                MyThread.setMessageFlag(false, i);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

}
