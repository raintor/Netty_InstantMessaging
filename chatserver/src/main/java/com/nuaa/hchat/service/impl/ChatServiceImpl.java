package com.nuaa.hchat.service.impl;

import com.nuaa.hchat.mapper.TbChatRecordMapper;
import com.nuaa.hchat.pojo.TbChatRecord;
import com.nuaa.hchat.pojo.TbChatRecordExample;
import com.nuaa.hchat.service.IChatService;
import com.nuaa.hchat.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/28 16:09
 * @Description:
 */
@Service
@Transactional
public class ChatServiceImpl implements IChatService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbChatRecordMapper tbChatRecordMapper;

    @Override
    public void insert(TbChatRecord record) {
        //对信息进行封装
        record.setId(idWorker.nextId());
        record.setCreatetime(new Date());
        record.setHasDelete(0);
        //0表示未读----先设置成未读
        record.setHasRead(0);
        tbChatRecordMapper.insert(record);
    }

    @Override
    public List<TbChatRecord> findAllChatRecord(String userid, String friendid) {


        //todo:hasdone--打开聊天窗口的时候，未读的消息表示为已读


        //根据userid和friendid查询u->f的信息和f->u的信息
        TbChatRecordExample chatRecordExample = new TbChatRecordExample();
        //由于需要查询两种情况，所以需要添加两种判定条件
        TbChatRecordExample.Criteria criteriauf = chatRecordExample.createCriteria();
        TbChatRecordExample.Criteria criteriafu = chatRecordExample.createCriteria();

        //u-》f
        criteriauf.andUseridEqualTo(userid);
        criteriauf.andFriendidEqualTo(friendid);

        //f->u
        criteriafu.andUseridEqualTo(friendid);
        criteriafu.andFriendidEqualTo(userid);

        //将where中两种判断跳进组装，其实在内部是利用一个List来实现的
        //在实际查询个的时候，通过遍历来将两种情况进行组合
        chatRecordExample.or(criteriafu);
        chatRecordExample.or(criteriauf);

        //查询别人发送过来的未读消息
        TbChatRecordExample msgexample = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = msgexample.createCriteria();
        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);

        List<TbChatRecord> tbChatRecords1 = tbChatRecordMapper.selectByExample(msgexample);
        tbChatRecords1.stream().forEach(record->{
            record.setHasRead(1);
            tbChatRecordMapper.updateByPrimaryKey(record);
        });

        List<TbChatRecord> tbChatRecords = tbChatRecordMapper.selectByExample(chatRecordExample);

        return tbChatRecords;


    }

    @Override
    public List<TbChatRecord> findNotReadReq(String userid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = example.createCriteria();

        //由于取数据查当前用户没有读取的消息，所以就是别人发给我的，所以当前用户在未读消息中，属于别人发送的那个朋友
        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);

        return tbChatRecordMapper.selectByExample(example);

    }

    @Override
    public void updateMsgState(String id) {
        TbChatRecord chatRecord = tbChatRecordMapper.selectByPrimaryKey(id);
        chatRecord.setHasRead(1);

        tbChatRecordMapper.updateByPrimaryKey(chatRecord);
    }
}
