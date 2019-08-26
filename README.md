# Netty_InstantMessaging
智能工厂人机交互及时通讯系统

### 描述：
基于Spring+Netty+Mybatis构建的多设备人机交互系统，实现基本的单、群监控以及实时状态监控功能。用户可以在客户端对设备进行管理，包括添加与移除需要监测的设备；同时可以与设备进行沟通，及时获取当前的状态信息，设备是否在线等。当设备发生异常状态时，及时将信息反馈，用户在客户端接收消息后及时进行介入处理。最后Netty的设计特点进行项目优化。

### 基本功能：
1：通讯协议的自定义设计与编解码；
2：设备添加与管理等功能；
3：心跳检测与空闲检测；设备在线与离线状态的获取；
4：设备消息及状态的获取:，控制消息已读/未读状态标记 ；
5：针对无状态处理器的单例模式优化改造。
