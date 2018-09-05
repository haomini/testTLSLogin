package com.example.zhiyicx.testtls.event;

import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMUserConfig;

import java.util.Observable;

/**
 * @Describe
 * @Author zhouhao
 * @Date 2018/3/16
 * @Contact 605626708@qq.com
 */

public class ConnectionEvent extends Observable implements TIMConnListener {

    private volatile static ConnectionEvent instance;

    private ConnectionEvent(){
        //注册消息监听器
    }

    public TIMUserConfig init(TIMUserConfig config) {
        config.setConnectionListener(this);
        return config;
    }

    public static ConnectionEvent getInstance(){
        if (instance == null) {
            synchronized (MessageEvent.class) {
                if (instance == null) {
                    instance = new ConnectionEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public void onConnected() {
        setChanged();
        notifyObservers(NotifyType.CONNECTED);
    }

    @Override
    public void onDisconnected(int i, String s) {
        setChanged();
        notifyObservers(NotifyType.DISCONNECTED);
    }

    @Override
    public void onWifiNeedAuth(String s) {

    }

    /**
     * 清理连接监听
     */
    public void clear(){
        instance = null;
    }

    public enum NotifyType{
        CONNECTED,//已连接
        DISCONNECTED,//失去连接
    }
}
