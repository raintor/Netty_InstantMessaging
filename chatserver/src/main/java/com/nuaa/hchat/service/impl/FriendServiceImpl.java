package com.nuaa.hchat.service.impl;

import com.nuaa.hchat.commen.Const;
import com.nuaa.hchat.mapper.TbFriendMapper;
import com.nuaa.hchat.mapper.TbFriendReqMapper;
import com.nuaa.hchat.mapper.TbUserMapper;
import com.nuaa.hchat.pojo.*;
import com.nuaa.hchat.service.IFriendService;
import com.nuaa.hchat.service.IUserService;
import com.nuaa.hchat.utils.IdWorker;
import com.nuaa.hchat.vo.FriendReq;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/25 15:13
 * @Description:
 */
@Service
@Transactional
public class FriendServiceImpl implements IFriendService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private TbFriendMapper tbFriendMapper;

    @Autowired
    private TbUserMapper tbUserMapper;

    @Autowired
    private TbFriendReqMapper tbFriendReqMapper;



    @Override
    public Result addFriend(String fromUserid, String toUserid) {
        //首先校验是否可以添加
        TbUser friend = tbUserMapper.selectByPrimaryKey(toUserid);
        Result result = iUserService.checkIsAdd(friend, fromUserid);
        if(result.isSuccess()){
            //可以添加
            //发送添加请求，等待对方确认
            TbFriendReq request = new TbFriendReq();
            request.setFromUserid(fromUserid);
            request.setToUserid(toUserid);
            IdWorker idWorker = new IdWorker();
            request.setId(idWorker.nextId());
            request.setCreatetime(new Date());
            request.setStatus(Const.HANDLER_STATES.NOT_HANDLE);

            int insert = tbFriendReqMapper.insert(request);
            if(insert>0){
                return new Result(Const.Issuccess.SUCCESS,"申请成功,等待对方添加");
            }
        }
        return result;
    }

    @Override
    public List<FriendReq> getFriendReq(String userid) {
        //首先到请求表中查询请求
        TbFriendReqExample reqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria reqCriteria = reqExample.createCriteria();
        //在请求列表中，由于是当前用户被请求，所以查询的是touserid
        reqCriteria.andToUseridEqualTo(userid);
        //只查找未处理的请求
        reqCriteria.andStatusEqualTo(Const.HANDLER_STATES.NOT_HANDLE);
        List<TbFriendReq> tbFriendReqs = tbFriendReqMapper.selectByExample(reqExample);
        //最终返回的是请求用户的信息
        List<FriendReq> reqs = new ArrayList<>();

        tbFriendReqs.stream().forEach(friendReq -> {
            TbUser tbUser = tbUserMapper.selectByPrimaryKey(friendReq.getFromUserid());
            FriendReq friendReqmsg = new FriendReq();
            BeanUtils.copyProperties(tbUser,friendReqmsg);
            friendReqmsg.setId(friendReq.getId());
            reqs.add(friendReqmsg);


        });

        return reqs;

    }

    /**
     * 同意申请
     * @param reqid
     * @return
     */
    @Override
    public Result acceptReq(String reqid) {
        //首先根据请求的id查询出请求
        TbFriendReq req = tbFriendReqMapper.selectByPrimaryKey(reqid);
        //设置该请求已经处理
        req.setStatus(Const.HANDLER_STATES.HAS_HANDLE);
        tbFriendReqMapper.updateByPrimaryKeySelective(req);
        //在好友表中相互添加互为好友
        TbFriend friendA = new TbFriend();
        TbFriend friendB = new TbFriend();
        IdWorker idWorker = new IdWorker();
        //获取好友间的id
        String AId = req.getToUserid();
        String BId = req.getFromUserid();
        //好友A
        friendA.setId(idWorker.nextId());
        friendA.setUserid(AId);
        friendA.setFriendsId(BId);
        friendA.setCreatetime(new Date());

        //好友B
        friendB.setId(idWorker.nextId());
        friendB.setUserid(BId);
        friendB.setFriendsId(AId);
        friendB.setCreatetime(new Date());

        //添加
        int insert1 = tbFriendMapper.insert(friendA);
        int insert = tbFriendMapper.insert(friendB);

        if(insert>0&&insert1>0){
            return new  Result(Const.Issuccess.SUCCESS,"添加成功");
        }

        return new Result(Const.Issuccess.FAILED,"添加好友失败");


    }

    /**
     * 拒绝好友请求
     */
    @Override
    public Result ignorReq(String reqid) {
        //拒绝好友请求的方法很简单，就是直接把申请状态改为1就可以了。
        TbFriendReq tbFriendReq = tbFriendReqMapper.selectByPrimaryKey(reqid);
        tbFriendReq.setStatus(Const.HANDLER_STATES.HAS_HANDLE);

        int i = tbFriendReqMapper.updateByPrimaryKeySelective(tbFriendReq);
        if(i>0){
            return new Result(Const.Issuccess.SUCCESS,"已拒绝");
        }

        return new Result(Const.Issuccess.FAILED,"拒绝失败");

    }

    /**
     * 获取好友列表(通讯录)
     */
    @Override
    public List<User> findFriendByUserid(String userid) {
        //到朋友数据库中根据自己id查找到好友
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria criteria = friendExample.createCriteria();

        criteria.andUseridEqualTo(userid);

        List<TbFriend> tbFriends = tbFriendMapper.selectByExample(friendExample);
        //设置返回给前端的list
        List<User> friensds = new ArrayList<>();
        tbFriends.stream().forEach(friendMapper->{
            TbUser tbUser = tbUserMapper.selectByPrimaryKey(friendMapper.getFriendsId());
            User friend = new User();
            BeanUtils.copyProperties(tbUser,friend);

            friensds.add(friend);
        });

        return friensds;
    }
}
