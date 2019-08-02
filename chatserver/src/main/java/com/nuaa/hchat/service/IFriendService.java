package com.nuaa.hchat.service;

import com.nuaa.hchat.vo.FriendReq;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/25 15:12
 * @Description:
 */
public interface IFriendService {
    Result addFriend(String fromUserid, String toUserid);

    List<FriendReq> getFriendReq(String userid);

    Result acceptReq(String reqid);

    Result ignorReq(String reqid);

    List<User> findFriendByUserid(String userid);
}
