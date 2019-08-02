package com.nuaa.hchat.commen;

/**
 * @author: raintor
 * @Date: 2019/6/22 16:38
 * @Description:
 */
public class Const {

   public static interface Issuccess{
        boolean SUCCESS = true;
        boolean FAILED = false;
    }

    //生成二维码的固定前缀
    public static final String QR_PREFIX = "hchat://";

   //生成二维码的固定后缀
    public static final String QR_DUFFIX = ".png";


    //定义好友请求的处理状态
    public interface HANDLER_STATES{
        int NOT_HANDLE = 0;
        int HAS_HANDLE = 1;
    }
}
