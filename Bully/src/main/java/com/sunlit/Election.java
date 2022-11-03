package com.sunlit;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class Election {

    public static ReentrantLock pingLock = new ReentrantLock(),
            electionLock = new ReentrantLock();
    private static boolean isElection = false, isPing = true;
    public static Process electionDetector;

    public static int[] processIds;

    public static Process getElectionDetector() {
        return electionDetector;
    }

    public static void setElectionDetector(Process electionDetector) {
        Election.electionDetector = electionDetector;
    }

    public static boolean isPingFlag() {
        return isPing;
    }

    public static void setPingFlag(boolean pingFlag) {
        Election.isPing = pingFlag;
    }

    public static boolean isElectionFlag() {
        return isElection;
    }

    public static void setElectionFlag(boolean electionFlag) {
        Election.isElection = electionFlag;
    }

    public static void initialElection(MyThread[] t) {

        processIds = new int[t.length];

        int maxPid = 0;

        for (int i = 0; i < t.length; i++) {
            processIds[i] = t[i].getProcess().getPid();
            if (maxPid<t[i].getProcess().getPid()) {
               maxPid=t[i].getProcess().getPid();
            }
        }
        for (int i = 0; i < processIds.length; i++) {
            if (processIds[i] == maxPid) {
                t[i].getProcess().isCoordinator = true;
            }
        }
        Arrays.sort(processIds);
        System.out.println("processIds: " + Arrays.toString(processIds));
    }
}