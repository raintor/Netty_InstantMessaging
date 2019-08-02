package com.nuaa.hchat.netty;

/**
 * @author: raintor
 * @Date: 2019/6/27 19:15
 * @Description:
 */

import com.nuaa.hchat.pojo.TbChatRecord;

/**
 * 定义相互间交流的消息实体
 */
public class Message {
    //消息类型：
    /**
     *  MSG_TYPE_CONN: 0,		// 连接
     * 	MSG_TYPE_SEND: 1,		// 发送消息
     * 	MSG_TYPE_REC: 2,		// 签收
     * 	MSG_TYPE_KEEPALIVE: 3,	// 客户端保持心跳
     * 	MSG_TYPE_RELOADFRIEND: 4	// 重新拉取好友
     */
    private Integer type;

    //封装具体的消息，其中包括
    /**
     * id
     * 发送方id userid
     * 接收方id friendid
     * 消息内容  msg
     */
    private TbChatRecord chatRecord;

    //扩展内容
    private Object ext;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public TbChatRecord getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(TbChatRecord chatRecord) {
        this.chatRecord = chatRecord;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
