package com.sunlit.client;


import com.sunlit.service.Service;

import java.util.ArrayList;
import java.util.Arrays;

public class TestClient {
    public static void main(String[] args) {
        // 构建一个使用java Socket/ netty/....传输的客户端
        RPCClient rpcClient = new NettyRPCClient("127.0.0.1", 8899);
        // 把这个客户端传入代理客户端
        RPCClientProxy rpcClientProxy = new RPCClientProxy(rpcClient);

        Service service = rpcClientProxy.getProxy(Service.class);

        System.out.println(service.Add(1, 2));
        System.out.println(service.StrCat("hello", "world"));
        ArrayList<Integer> a = new ArrayList<>(Arrays.asList(9,5,3,7,1));
        System.out.println(service.Sort(a, 5));

    }
}
