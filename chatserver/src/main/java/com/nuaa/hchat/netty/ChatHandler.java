package com.nuaa.hchat.netty;

import com.alibaba.fastjson.JSON;
import com.nuaa.hchat.pojo.TbChatRecord;
import com.nuaa.hchat.service.IChatService;
import com.nuaa.hchat.service.impl.ChatServiceImpl;
import com.nuaa.hchat.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author: raintor
 * @Date: 2019/6/27 15:31
 * @Description:
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        //获取用户发送过来的消息
        String text = msg.text();
        System.out.println("用户发送过来的消息是" + text);
        //解析发送过来的json
        Message message = JSON.parseObject(text, Message.class);

        //获取service
        IChatService chatService = SpringUtil.getBean(ChatServiceImpl.class);

        int type = message.getType();
        switch (type) {
            case 0:
                //表示用户连接进来，我们需要进行通道关联
                String userid = message.getChatRecord().getUserid();
                //ctx获得的通道是用户连接进的通道
                UserChannelMap.put(userid, ctx.channel());
                System.out.println("用户" + userid + "连接");
                break;
            case 1:

                //表示聊天信息
                //首先要保存用户的聊天信息，在普通类中获取spring容器，使用ApplicationContext类，对此我们进行了封装成工具类
                TbChatRecord record = message.getChatRecord();
                chatService.insert(record);
                System.out.println("接收到用户消息"+record.getMessage());
                //如果用户在线，直接发送
                //通过判断好用的通道是否在线来判断
                Channel friendChannel = UserChannelMap.getFriendChannel(record.getFriendid());
                if(friendChannel!=null){
                    //说明好友在线
                    //发送给好友，使用好友的channel
                    friendChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));

                }else{
                    //不在线，暂时不发送
                    System.out.println("用户不在线");
                }

                break;
            case 2:
                //消息类型是2，表示发送的是消息正在聊天的消息，将消息谁已读
                chatService.updateMsgState(message.getChatRecord().getId());
                break;
            case 3:
                //接受心跳消息
                System.out.println("心跳消息"+JSON.toJSONString(message));
                break;
        }


    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        group.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户断开连接");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.channel().close();
    }
}
