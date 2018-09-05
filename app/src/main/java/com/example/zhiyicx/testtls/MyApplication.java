package com.example.zhiyicx.testtls;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.example.zhiyicx.testtls.utils.Constant;
import com.example.zhiyicx.testtls.utils.Foreground;
import com.tencent.bugly.imsdk.crashreport.CrashReport;
import com.tencent.imsdk.TIMGroupReceiveMessageOpt;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMSdkConfig;

/**
 * @Describe
 * @Author zhouhao
 * @Date 2018/7/18
 * @Contact 605626708@qq.com
 */
public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        initTIM();
    }

    /**
     * 初始化TIM, 添加部分配置
     */
    private void initTIM() {

        // IM 只在主进程初始化
        if (isMainProcess()) {

            //初始化IMSDK
            TIMSdkConfig config;
            config = new TIMSdkConfig(Constant.SDK_APPID);
            // 打开bugly crash上报
            config.enableCrashReport(true);
            CrashReport.initCrashReport(getApplicationContext(), "a4ef5b4743", true);
            TIMManager.getInstance().init(this, config);

            //初始化TLS
            TlsBusiness.init(getApplicationContext());

            Foreground.init(this);
            TIMManager.getInstance().setOfflinePushListener(notification -> {
                if (notification.getGroupReceiveMsgOpt() == TIMGroupReceiveMessageOpt.ReceiveAndNotify) {
                    //消息被设置为需要提醒
                    notification.doNotify(getApplicationContext(), R.mipmap.ic_launcher);
                }
            });
        }
    }

    /**
     * 包名判断是否为主进程
     *
     * @param
     * @return
     */
    public boolean isMainProcess() {
        return getApplicationContext().getPackageName().equals(getCurrentProcessName());
    }

    /**
     * 获取当前进程名
     */
    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }
}
