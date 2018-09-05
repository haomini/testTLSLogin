package com.example.zhiyicx.testtls;

import android.content.Context;

import com.example.zhiyicx.testtls.utils.Constant;

import tencent.tls.platform.TLSAccountHelper;
import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/7/8.
 */
public class TLSService {

    private TLSLoginHelper loginHelper;
    private TLSAccountHelper accountHelper;

    private static int lastErrno = -1;

    private static TLSService tlsService = null;

    private TLSService() {
    }

    public static TLSService getInstance() {
        if (tlsService == null) {
            tlsService = new TLSService();
        }
        return tlsService;
    }

    /**
     * @param context: 关联的activity
     * @function: 初始化TLS SDK, 必须在使用TLS SDK相关服务之前调用
     */
    public void initTlsSdk(Context context) {

//        QALSDKManager.getInstance().init(context.getApplicationContext(), Constant.SDK_APPID);

        loginHelper = TLSLoginHelper.getInstance().init(context.getApplicationContext(),
                Constant.SDK_APPID, Constant.ACCOUNT_TYPE, Constant.APP_VERSION);
        loginHelper.setTimeOut(Constant.TIMEOUT);
        loginHelper.setLocalId(Constant.LANGUAGE_CODE);
        loginHelper.setTestHost("", true);                   // 走sso

        accountHelper = TLSAccountHelper.getInstance().init(context.getApplicationContext(),
                Constant.SDK_APPID, Constant.ACCOUNT_TYPE, Constant.APP_VERSION);
        accountHelper.setCountry(Integer.parseInt(Constant.COUNTRY_CODE)); // 存储注册时所在国家，只须在初始化时调用一次
        accountHelper.setTimeOut(Constant.TIMEOUT);
        accountHelper.setLocalId(Constant.LANGUAGE_CODE);
        accountHelper.setTestHost("", true);                 // 走sso

    }

    public boolean needLogin(String identifier) {
        if (identifier == null)
            return true;
        return loginHelper.needLogin(identifier);
    }

    public String getLastUserIdentifier() {
        TLSUserInfo userInfo = getLastUserInfo();
        if (userInfo != null)
            return userInfo.identifier;
        else
            return null;
    }

    public TLSUserInfo getLastUserInfo() {
        return loginHelper.getLastUserInfo();
    }

    public String getUserSig(String identify) {
        return loginHelper.getUserSig(identify);
    }

    public void clearUserInfo(String identifier) {
        loginHelper.clearUserInfo(identifier);
        lastErrno = -1;
    }

    public int TLSPwdLogin(String identifier, String password, TLSPwdLoginListener listener) {
        return loginHelper.TLSPwdLogin(identifier, password.getBytes(), listener);
    }
}
