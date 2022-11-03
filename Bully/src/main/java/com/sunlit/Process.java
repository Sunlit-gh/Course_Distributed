package com.sunlit;

import lombok.Data;

@Data
public class Process {
    int pid;
    boolean isCoordinator, isDown;
    public Process(int pid) {
        this.pid = pid;
        this.isDown = false;
        this.isCoordinator = false;
    }
}
