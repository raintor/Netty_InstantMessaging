package com.nuaa.hchat.controller;

import com.nuaa.hchat.commen.Const;
import com.nuaa.hchat.pojo.TbUser;
import com.nuaa.hchat.service.IUserService;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/22 15:04
 * @Description:
 */
@RestController()
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * for test
     * @return
     */
    /**
     * 整合好SpringBoot的调试类
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<TbUser> findAllUser(){
        return iUserService.findAll();
    }

    /**
     * 用户登陆
     * @param tbUser
     * @return
     */
    @RequestMapping("/login")
    public Result<User> login(@RequestBody TbUser tbUser) {
        try {
            if(tbUser==null)
                return new Result(Const.Issuccess.FAILED,"请输入用户名和密码");
            User _user = iUserService.login(tbUser.getUsername(),tbUser.getPassword());
            if(_user==null){
                return new Result(Const.Issuccess.FAILED,"登陆失败，请校验用户名密码是否正确");
            }else {
                return new Result<User>(Const.Issuccess.SUCCESS, "登陆成功", _user);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(Const.Issuccess.FAILED,"登陆失败");
        }
    }

    /**
     * 用户注册
     * @param tbUser
     * @return
     */
    @RequestMapping("/register")
    public Result<String> register(@RequestBody TbUser tbUser){
        if(tbUser==null){
            return new Result(Const.Issuccess.FAILED,"请输入注册信息");
        }
        return iUserService.register(tbUser);
    }

    /**
     * 用户上传头像
     */
    @RequestMapping("/upload")
    public Result<User> uploadFile(MultipartFile file , String userid){
        User user = iUserService.upload(file,userid);
        if(user==null){
            return new Result(Const.Issuccess.FAILED,"上传头像失败");
        }else {
            return new Result<User>(Const.Issuccess.SUCCESS,"上传成功",user);
        }
    }

    /**
     * 用户信息修改
     */
    @RequestMapping("/updateNickname")
    public Result<String> updateUserInfo(@RequestBody TbUser tbUser){
        if(tbUser==null){
            return new Result<String>(Const.Issuccess.FAILED,"请输入用户信息");
        }
        return iUserService.updateInfo(tbUser.getId(),tbUser.getNickname());
    }
    /**
     * 根据id查找用户信息
     *
     */
    @RequestMapping( "/findById")
    public User findUserById(String userid){
        return iUserService.findByUerId(userid);
    }
    /**
     * 搜索好友
     */
    @RequestMapping("/findByUsername")
    public Result<User> findBYUserName(String userid,String friendUsername){
       return iUserService.findByUsername(userid,friendUsername);
    }

}
