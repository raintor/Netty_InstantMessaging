package com.nuaa.hchat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author: raintor
 * @Date: 2019/6/27 15:06
 * @Description:
 */
public class WebsocketInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //用于支持http协议的编解码器
        pipeline.addLast(new HttpServerCodec());
        //支持大数据流---=块
        pipeline.addLast(new ChunkedWriteHandler());

        // 添加对HTTP请求和响应的聚合器:只要使用Netty进行Http编程都需要使用
        // 对HttpMessage进行聚合，聚合成FullHttpRequest或者FullHttpResponse
        // 在netty编程中都会使用到Handler
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        //用于支持websocket
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //添加空闲检测
        pipeline.addLast(new IdleStateHandler(4,8,12));

        //添加心跳检测---自己定义的
        pipeline.addLast(new HearBeatHandler());

        //天机自己定义的Handler
        pipeline.addLast(new ChatHandler());
    }
}
