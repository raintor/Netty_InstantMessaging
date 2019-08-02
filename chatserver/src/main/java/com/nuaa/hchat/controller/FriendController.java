package com.nuaa.hchat.controller;

import com.nuaa.hchat.pojo.TbFriendReq;
import com.nuaa.hchat.service.IFriendService;
import com.nuaa.hchat.vo.FriendReq;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/25 15:10
 * @Description:
 */
@RestController
@RequestMapping("/friend/")
public class FriendController {



    @Autowired
    private IFriendService iFriendService;

    /**
     * 添加好友的申请
     */
    @RequestMapping("sendRequest")
    public Result addFriend(@RequestBody TbFriendReq friendReq){
        return iFriendService.addFriend(friendReq.getFromUserid(),friendReq.getToUserid());
    }


    /**
     * 好友请求加载
     * @param userid 当前登录用户的id
     */
    @RequestMapping("findFriendReqByUserid" )
    public List<FriendReq> getFriendReq(String userid){
        return iFriendService.getFriendReq(userid);
    }

    /**
     * 同意好友申请
     */
    @RequestMapping("acceptFriendReq")
    public Result acceptReq(String reqid){
        return iFriendService.acceptReq(reqid);
    }

    /**
     * 拒绝好友请求
     */
    @RequestMapping("ignoreFriendReq")
    public Result ignorFriendReq(String reqid){
        return iFriendService.ignorReq(reqid);
    }

    /**
     * 通讯录功能
     */
    @RequestMapping("findFriendByUserid")
    public List<User> findFriendByUserid(String userid){
        return iFriendService.findFriendByUserid(userid);
    }

}
