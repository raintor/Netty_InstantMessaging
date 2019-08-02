package com.nuaa.hchat.service.impl;

import com.nuaa.hchat.commen.Const;
import com.nuaa.hchat.mapper.TbFriendMapper;
import com.nuaa.hchat.mapper.TbFriendReqMapper;
import com.nuaa.hchat.mapper.TbUserMapper;
import com.nuaa.hchat.pojo.*;
import com.nuaa.hchat.service.IUserService;
import com.nuaa.hchat.utils.FastDFSClient;
import com.nuaa.hchat.utils.IdWorker;
import com.nuaa.hchat.utils.QRCodeUtils;
import com.nuaa.hchat.vo.Result;
import com.nuaa.hchat.vo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author: raintor
 * @Date: 2019/6/22 15:08
 * @Description:
 */
@Service("iUserService")
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private FastDFSClient fastDFSClient;

    //该类是spring中的环境抽象配置类，可用于获取properties的信息
    @Autowired
    private Environment evn;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private TbFriendMapper tbFriendMapper;

    @Autowired
   private TbFriendReqMapper tbFriendReqMapper;
    @Override
    public List<TbUser> findAll() {
        return userMapper.selectByExample(null);
    }

    @Override
    public User login(String username, String password) {
       if(StringUtils.isNotBlank(username)&&StringUtils.isNotBlank(password)){
           TbUserExample example = new TbUserExample();
           TbUserExample.Criteria criteria = example.createCriteria();
           criteria.andUsernameEqualTo(username);

           List<TbUser> tbUsers = userMapper.selectByExample(example);
           if(tbUsers!=null&&tbUsers.size()==1){
               //将密码MD5加密，然后校验
               String encodingPasswd = DigestUtils.md5DigestAsHex(password.getBytes());
               if(StringUtils.equals(encodingPasswd,tbUsers.get(0).getPassword())){
                   //密码正确
                   //将信息封装返回
                   User user = new User();
                   BeanUtils.copyProperties(tbUsers.get(0),user);
                   return user;
               }
           }
       }
       return null;
    }

    @Override
    public Result<String> register(TbUser tbUser) {
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(tbUser.getUsername());
        List<TbUser> tbUsers = userMapper.selectByExample(example);

        if(tbUsers!=null&&tbUsers.size()>0){
            return new Result<String>(Const.Issuccess.FAILED,"用户已经存在");

        }else {
            try {
                //用户不存在，可以注册
                //首先设置用户id-----根据雪花算法设置
                tbUser.setId(new IdWorker().nextId());
                //将密码进行MD5加密
                tbUser.setPassword(DigestUtils.md5DigestAsHex(tbUser.getPassword().getBytes()));
                tbUser.setNickname(tbUser.getUsername());
                tbUser.setCreatetime(new Date());
                //todo

                tbUser.setPhone("");
                tbUser.setPicNormal("");
                tbUser.setPicSmall("");
                tbUser.setSign("");
                //add：用户注册的时候添加二维码
                //生成的二维码先临时保存，然后在上传到文件服务器
                String temppath = evn.getProperty("hcat.tmpdir");
                String conten = Const.QR_PREFIX+tbUser.getUsername();
                temppath = temppath+tbUser.getUsername()+Const.QR_DUFFIX;
                qrCodeUtils.createQRCode(temppath,conten);
                //将临时生成的二维码保存到FASTDFS
                String url = fastDFSClient.uploadFile(new File(temppath));
                //生成完成的url带有服务器ip的
                String realurl = evn.getProperty("fdfs.httpurl")+url;

                tbUser.setQrcode(realurl);

                int insert = userMapper.insert(tbUser);
                if(insert>0){
                    return new Result<String>(Const.Issuccess.SUCCESS,"新建用户成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new Result<String>(Const.Issuccess.FAILED,"新建用户失败");
            }


        }
        return new Result<String>(Const.Issuccess.FAILED,"新建用户失败");
    }

    @Override
    public User upload(MultipartFile file, String userid) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userid);
        if(tbUser !=null){
            try {
                //如果不为空，则进行使用工具类上传，该上传会返回一个url
                //但是该url路径是不带  http：//192.168.64.133/这样的文件服务器地址，我们后续要自己封装
                String url = fastDFSClient.uploadFile(file);
                //调试
                System.out.println(url);
                //文件上传的时候，会子动生成一个缩略图，
                //该缩略图的配置信息，是在application。properties中的，根据像素来命名的
                //命名规则：文件名_150x150.后缀名
                //根据返回的url来获取文件名和后缀吗
                String[] filenames = url.split("\\.");
                String filename = filenames[0];
                String suffix = filenames[1];

//                fastDFSClient.uploadFile(new File(picSmallUrl));
                //利用spring中的 Environment类来获取服务器的地址
                String serverurl = evn.getProperty("fdfs.httpurl");
                //组装缩略图url
                //String picSmallUrl = filename+"_150x150."+suffix;
                String picSmallUrl = url;
                //设置为用户信息
                //大图片
                tbUser.setPicNormal(serverurl+url);
                //缩略图
                tbUser.setPicSmall(serverurl+picSmallUrl);
                //更新到数据库
                userMapper.updateByPrimaryKey(tbUser);
                //返回User信息
                User user = new User();
                BeanUtils.copyProperties(tbUser,user);
                return user;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }

    @Override
    public Result<String> updateInfo(String id, String nickname) {
        if(StringUtils.isNotBlank(nickname)){
            TbUser tbUser = userMapper.selectByPrimaryKey(id);
            tbUser.setNickname(nickname);
            int i = userMapper.updateByPrimaryKey(tbUser);
            if(i>0){
                return new Result<String>(Const.Issuccess.SUCCESS,"更新用户信息成功");
            }else {
                return new Result<String>(Const.Issuccess.FAILED,"更新用户信息失败");
            }

        }
        return new Result<String>(Const.Issuccess.FAILED,"请输入需要更新的名称");
    }

    @Override
    public User findByUerId(String userid) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userid);
        if(tbUser!=null){
            User user = new User();
            BeanUtils.copyProperties(tbUser,user);
            return user;
        }
        return null;
    }

    /**
     * 搜索好友，
     * 搜索的时候，可以不对用户进行校验，等到添加的时候在校验
     * @param userid
     * @param friendUsername
     * @return
     */
    @Override
    public Result<User> findByUsername(String userid, String friendUsername) {
        //用户搜索好友，首先不能添加已经是好友的了，其次不能添加自己，已经提交的不能在申请
        //首先查询到好友信息
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(friendUsername);

        List<TbUser> frindes = userMapper.selectByExample(example);
        if(frindes!=null&&frindes.size()>0){
            TbUser friend = frindes.get(0);


            /**
             * //has do ： 对代码进行了抽取
             * {@code checkIsAdd}
             */

            //如果成功，则返回用户信息
            User friendUser = new User();
            BeanUtils.copyProperties(friend,friendUser);
            return new Result<User>(Const.Issuccess.SUCCESS,"请求成功！",friendUser);

        }

        return new Result(Const.Issuccess.FAILED,"申请失败,该用户不存在");

    }

    /**
     * 判断用户是否可以添加该好友
     * @param friend  用户搜索的朋友
     * @param userid  用户id
     * @return
     */
    public Result checkIsAdd(TbUser friend,String userid){

        //1：不能添加自己
        if(friend.getId().equals(userid)){
            return new Result(Const.Issuccess.FAILED,"不能添加自己为好友");
        }
        //2：不能重复添加   这里使用好友表
        TbFriendExample isfriendExample = new TbFriendExample();
        TbFriendExample.Criteria criteriaf = isfriendExample.createCriteria();
        criteriaf.andFriendsIdEqualTo(friend.getId());
        criteriaf.andUseridEqualTo(userid);

        List<TbFriend> tbFriends = tbFriendMapper.selectByExample(isfriendExample);
        if(tbFriends!=null&&tbFriends.size()>0){
            return new Result(Const.Issuccess.FAILED,"不能重复添加好友");
        }

        //3:判断是否已经添加好友申请，这里使用请求信息表，同时注意请求是没有处理的认为是正在申请中
        TbFriendReqExample reqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria criteriareq = reqExample.createCriteria();

        //封装信息
        criteriareq.andFromUseridEqualTo(userid);
        criteriareq.andToUseridEqualTo(friend.getId());
        //必须是未处理的
        criteriareq.andStatusEqualTo(0);

        List<TbFriendReq> tbFriendReqs = tbFriendReqMapper.selectByExample(reqExample);
        if(tbFriendReqs!=null&&tbFriendReqs.size()>0){
            return new Result(Const.Issuccess.FAILED,"不能重复申请");
        }

        return new Result(Const.Issuccess.SUCCESS,"可以添加");
    }
}
