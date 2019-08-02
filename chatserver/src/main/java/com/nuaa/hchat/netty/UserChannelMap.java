package com.nuaa.hchat.netty;

/**
 * @author: raintor
 * @Date: 2019/6/27 19:21
 * @Description:
 */

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装用户连接进来以后，用户id与channel的关系封装
 */
public class UserChannelMap {
    private static Map<String, Channel> user_channel;

    static {
        user_channel = new HashMap<>();
    }

    public static void put(String userid,Channel channel){
        user_channel.put(userid,channel);
    }

    /**
     * 根据用户id移除关联
     * @param userid
     */
    public static void remove(String userid){
        user_channel.remove(userid);
    }

    public static void removeByChannelId(String channelid){
        if(StringUtils.isNotBlank(channelid)){
            for(String s :user_channel.keySet()){
                Channel channel = user_channel.get(s);
                if(channelid.equals(channel.id().asLongText())){
                    user_channel.remove(s);
                    break;
                }
            }
        }
    }

    /**
     * 获取好友的通道
     * @param friendid
     * @return
     */
    public static Channel getFriendChannel(String friendid) {
        return user_channel.get(friendid);
    }
}
