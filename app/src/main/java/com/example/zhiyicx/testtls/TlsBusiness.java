package com.example.zhiyicx.testtls;

import android.content.Context;

/**
 * 初始化tls登录模块
 */
public class TlsBusiness {


    private TlsBusiness(){}

    public static void init(Context context){
        TLSService.getInstance().initTlsSdk(context);
    }

    public static void logout(String id){
        TLSService.getInstance().clearUserInfo(id);
    }
}
