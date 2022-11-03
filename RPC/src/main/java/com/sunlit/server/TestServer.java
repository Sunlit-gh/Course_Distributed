package com.sunlit.server;


import com.sunlit.service.Service;
import com.sunlit.service.ServiceImpl;

public class TestServer {
    public static void main(String[] args) {

        Service service = new ServiceImpl();

        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.provideServiceInterface(service);

        RPCServer RPCServer = new NettyRPCServer(serviceProvider);
        RPCServer.start(8899);
    }
}