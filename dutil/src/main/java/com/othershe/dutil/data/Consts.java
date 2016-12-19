package com.othershe.dutil.data;

public class Consts {
    public static final int NONE = 0; //无状态
    public static final int START = 1; //准备下载
    public static final int PROGRESS = 2; //下载中
    public static final int PAUSE = 3; //暂停
    public static final int RESUME = 4; //继续下载
    public static final int CANCEL = 5; //取消
    public static final int RESTART = 6; //重新下载
    public static final int FINISH = 7; //下载完成
    public static final int ERROR = 8; //下载出错
    public static final int DESTROY = 9; //释放资源
}
