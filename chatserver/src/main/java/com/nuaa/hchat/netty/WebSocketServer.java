package com.nuaa.hchat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author: raintor
 * @Date: 2019/6/27 15:01
 * @Description:
 */
@Component
public class WebSocketServer {
    private EventLoopGroup boosGroup;
    private EventLoopGroup workerGroup;

    private ServerBootstrap bootstrap;

    public WebSocketServer(){
        boosGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WebsocketInitializer());

    }

    public void start(){

        try {
            ChannelFuture sync = bootstrap.bind(9001).sync();
            System.out.println("聊天服务器启动成功");
            ChannelFuture future = sync.channel().closeFuture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
