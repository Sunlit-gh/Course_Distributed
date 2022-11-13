package com.sunlit;

import java.util.LinkedList;
import java.util.Queue;

public class MyLock implements Lock {

    Queue<Thread> queue = new LinkedList<>();

    @Override
    public void acquire() throws Exception {
        synchronized (this) {
            Thread currentThread = Thread.currentThread();
            queue.add(currentThread);
            while (queue.peek() != currentThread) {
                this.wait();
            }
        }
    }

    @Override
    public void release() {
        synchronized (this) {
            queue.poll();
            this.notifyAll();
        }
    }
}
