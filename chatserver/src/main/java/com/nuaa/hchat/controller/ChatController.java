package com.nuaa.hchat.controller;

import com.nuaa.hchat.pojo.TbChatRecord;
import com.nuaa.hchat.service.IChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/28 16:50
 * @Description:
 */
@RestController
@RequestMapping("/chatrecord/")
public class ChatController {

    @Autowired
    private IChatService iChatService;

    @RequestMapping("findByUserIdAndFriendId")
    public List<TbChatRecord> findByUserIdAndFriendId(String userid,String friendid){
        if(StringUtils.isNotBlank(userid)&&StringUtils.isNotBlank(friendid)){
            return iChatService.findAllChatRecord(userid,friendid);
        }else {
            return null;
        }
    }

    @RequestMapping("findUnreadByUserid")
    public List<TbChatRecord> findNotReadReq(String userid){
        if(StringUtils.isNotBlank(userid)){
            return iChatService.findNotReadReq(userid);
        }else {
            return null;
        }
    }

}
