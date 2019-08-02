package com.nuaa.hchat.service;

import com.nuaa.hchat.pojo.TbUser;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/22 15:07
 * @Description:
 */
public interface IUserService {
    List<TbUser> findAll();

    User login(String username, String password);

    Result<String> register(TbUser tbUser);

    User upload(MultipartFile file, String userid);

    Result<String> updateInfo(String id, String nickname);

    User findByUerId(String userid);

    Result<User> findByUsername(String userid, String friendUsername);

    public Result checkIsAdd(TbUser friend,String userid);
}
