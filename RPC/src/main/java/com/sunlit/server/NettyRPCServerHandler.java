package com.sunlit.server;


import com.sunlit.common.Request;
import com.sunlit.common.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 因为是服务器端，我们知道接受到请求格式是Request
 * Object类型也行，强制转型就行
 */
@AllArgsConstructor
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<Request> {
    private ServiceProvider serviceProvider;
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request msg) throws Exception {
        //System.out.println(msg);
        Response response = getResponse(msg);
        ctx.writeAndFlush(response);
        ctx.close();
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    Response getResponse(Request request) {
        // 得到服务名
        String interfaceName = request.getInterfaceName();
        // 得到服务端相应服务实现类
        Object service = serviceProvider.getService(interfaceName);
        // 反射调用方法
        Method method = null;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());
            return Response.success(invoke);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return Response.fail();
        }
    }
}
