package com.example.zhiyicx.testtls;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.zhiyicx.testtls.event.ConnectionEvent;
import com.example.zhiyicx.testtls.event.FriendshipEvent;
import com.example.zhiyicx.testtls.event.GroupEvent;
import com.example.zhiyicx.testtls.event.MessageEvent;
import com.example.zhiyicx.testtls.event.RefreshEvent;
import com.example.zhiyicx.testtls.utils.Constant;
import com.example.zhiyicx.testtls.utils.UserInfo;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSPwdLoginListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * @Describe 腾讯云通讯工具类
 * @Author zhouhao
 * @Date 2018/5/28
 * @Contact 605626708@qq.com
 */
public class TsTimManager {

    /**
     * 初始化群, 好友关系链, 和事件event
     */
    public static void initUserConfig(Context context) {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                goLoginAndCleanHistory();
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                showReLoginDialog(context, (dialog12, which) -> goLoginAndCleanHistory());
            }
        });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        userConfig = ConnectionEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
    }

    /**
     * 清空本地保存的登录用户信息, 然后跳转到登录页
     */
    public static void goLoginAndCleanHistory() {

    }

    /**
     * 1. 通过用户名获取用户聊天Tim登录所需的UserSig, 再下一步登录聊天使用
     *
     * @param loginUser 当前获取到的登录用户信息
     */
    public static void doTlsLogin(Object loginUser, FragmentActivity activity, OnTimLoginSuccessListener listener) {
        // TIM登录验证
        TLSService.getInstance().TLSPwdLogin(
                "ts_14",
                Constant.DEFAULT_PASSWORD, new TLSPwdLoginListener() {
                    @Override
                    public void OnPwdLoginSuccess(TLSUserInfo tlsUserInfo) {
                        // 登陆成功, 开始验证sign.
                        // TIM登录已经验证过后, 获取并存储userSig
                        String id = TLSService.getInstance().getLastUserIdentifier();
                        UserInfo.getInstance().setId(id);
                        UserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));

                        doTIMSigLogin(loginUser, activity, listener);
                    }

                    @Override
                    public void OnPwdLoginReaskImgcodeSuccess(byte[] bytes) {
                        // 图形验证码获取成功时, 显示图形验证码
                        Toast.makeText(activity, "需要图形验证码", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnPwdLoginNeedImgcode(byte[] bytes, TLSErrInfo tlsErrInfo) {
                        // 图形验证码获取成功时, 显示图形验证码
                        Toast.makeText(activity, "需要图形验证码", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnPwdLoginFail(TLSErrInfo tlsErrInfo) {
                        // 登录失败时
                        Toast.makeText(activity, "登录失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void OnPwdLoginTimeout(TLSErrInfo tlsErrInfo) {
                        // 超时
                        Toast.makeText(activity, "登录超时", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 2. 在通过Tls校验后拥有userSig时, 登录聊天服务器
     */
    public static void doTIMSigLogin(Object loginUser, final FragmentActivity activity, OnTimLoginSuccessListener listener) {
        // 登录之前要初始化群和好友关系链缓存
        initUserConfig(activity);
        // 登录聊天服务器
        TIMManager.getInstance().login(UserInfo.getInstance().getId(), UserInfo.getInstance().getUserSig(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                switch (i) {
                    case 6208:
                        // 离线状态下被其他终端踢下线
                        // 统一到TsTimManager的TIMUserStatusListener: onForceOffline()处理
                        break;
                    case 6200:
                        Toast.makeText(activity, "登录失败，当前无网络", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(activity, "登录失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onSuccess() {
                //初始化程序后台后消息推送
//                PushUtil.getInstance();
                //初始化消息监听
                MessageEvent.getInstance();
                // 如果需要单独集成小米, 华为, 魅族推送在这里配置, 另单独添加dependencies至gradle
                /*String deviceMan = android.os.Build.MANUFACTURER;
                // 注册小米和华为推送
                if (deviceMan.equals("Xiaomi") && PushUtil.shouldMiInit()){
                    MiPushClient.registerPush(Thinksns.getContext(), "2882303761517480335", "5411748055335");
                }else if (deviceMan.equals("HUAWEI")){
                    PushManager.requestToken(this);
                }

                // 魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
                if(MzSystemUtils.isBrandMeizu(getApplicationContext())){
                    com.meizu.cloud.pushsdk.PushManager.register(this, "112662", "3aaf89f8e13f43d2a4f97a703c6f65b3");
                }*/
                listener.onSuccess("success");
            }
        });
    }

    /**
     * 是否已有TIM用户登录
     */
    public static boolean isUserLogin() {
        return UserInfo.getInstance().getId() != null && (!TLSService.getInstance().needLogin("ts_14"));
    }

    public static void showReLoginDialog(Context activity, DialogInterface.OnClickListener listener) {
        Toast.makeText(activity, "token error", Toast.LENGTH_SHORT).show();
    }

    /**
     * 当tim登录完全成功时回调
     */
    public interface OnTimLoginSuccessListener {
        void onSuccess(Object modelUser);
    }
}
