package com.sunlit;

public interface Lock {

    void acquire() throws Exception;

    void release() throws Exception;


}
