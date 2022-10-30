import java.io.*;
import java.util.Random;

// 代表单一的选举信息
class Message {
    int processId;
    boolean isVictoryMsg;

    Message(int id, boolean isVictory) {
        processId = id;
        isVictoryMsg = isVictory;
    }
}

// 每个参与者的消息框，用于从对等方接收消息
class MessageBox {
    int entries, maxEntries;
    Message[] elements;

    public MessageBox(int number) {
        maxEntries = number;
        elements = new Message[maxEntries];
        entries = 0;
    }

    synchronized void send(Message msg) throws InterruptedException {
        while (entries == maxEntries) {
            System.out.println("来自 " + msg.processId + " 的消息正在等待");
            wait();
        }
        elements[entries] = msg;
        entries++;
        notifyAll();
    }

    synchronized Message recieve() throws InterruptedException {
        while (entries == 0)
            wait();
        Message x;
        x = elements[0];
        for (int i = 1; i < entries; i++)
            elements[i - 1] = elements[i];
        entries--;
        notifyAll();
        return x;
    }
}

//参与者
class Participant extends Thread {
    MessageBox inbox;
    MessageBox[] neighbour;
    int id;

    int leader;
    int self;

    public Participant(int id) {
        this.id = id;
    }

    public void run() {
        // 宣布自己为领导者并让他人知道
        leader = id;
        self = id;

        System.out.println(id + ": 开始选举进程");

        for (int i = 0; i < neighbour.length; i++) {
            try {
                System.out.println(id + ": 向邻居" + i + "发送选举信息");
                neighbour[i].send(new Message(self, false));
            } catch (Exception ignored) {
            }
        }

        try {
            while (true) {
                Message m = (Message) inbox.recieve();
                System.out.println(id + ": 收到来自" + m.processId + "的选举消息");

                if (m.processId > leader) {
                    leader = m.processId;
                    System.out.println(id + ": 收到来自较高进程ID的新消息。 选取 " + leader + " 作为当前领导者");
                }
            }
        } catch (Exception ignored) {
        }

    }
}

//霸道选举算法
public class bully {
    public static void main(String[] args) throws IOException {
        final int processNo = 4;

        Participant[] processes = new Participant[processNo];

        Random randomGenerator = new Random();

        // 创建选举参与者、他们的收件箱和邻居
        for (int i = 0; i < processNo; i++) {
            processes[i] = new Participant(randomGenerator.nextInt(100));
            processes[i].inbox = new MessageBox(processNo);
            processes[i].neighbour = new MessageBox[processNo];
        }

        for (int i = 0; i < processNo; i++) {
            for (int j = 0; j < processNo; j++)
                processes[i].neighbour[j] = processes[j].inbox;
        }

        // 启动选举进程

//        for (int i = 0; i < processNo; i++)
//            processes[i].start();
//
//        try {
//            Thread.sleep(100);
//        } catch (Exception e) {
//        }


        for (int i = 0; i < processNo; i++) {
            processes[i].interrupt();
            System.out.println(processes[i].id + ": 当选领导人为 " + processes[i].leader);
        }

        System.exit(0);
    }
}