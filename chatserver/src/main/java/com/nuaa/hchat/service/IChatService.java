package com.nuaa.hchat.service;

import com.nuaa.hchat.pojo.TbChatRecord;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/28 16:09
 * @Description:
 */
public interface IChatService {
    void insert(TbChatRecord record);

    List<TbChatRecord> findAllChatRecord(String userid, String friendid);

    List<TbChatRecord> findNotReadReq(String userid);

    void updateMsgState(String id);
}
