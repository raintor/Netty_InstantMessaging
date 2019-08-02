package com.nuaa.hchat.mapper;

import com.nuaa.hchat.pojo.TbFriend;
import com.nuaa.hchat.pojo.TbFriendExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbFriendMapper {
    int countByExample(TbFriendExample example);

    int deleteByExample(TbFriendExample example);

    int deleteByPrimaryKey(String id);

    int insert(TbFriend record);

    int insertSelective(TbFriend record);

    List<TbFriend> selectByExample(TbFriendExample example);

    TbFriend selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") TbFriend record, @Param("example") TbFriendExample example);

    int updateByExample(@Param("record") TbFriend record, @Param("example") TbFriendExample example);

    int updateByPrimaryKeySelective(TbFriend record);

    int updateByPrimaryKey(TbFriend record);
}