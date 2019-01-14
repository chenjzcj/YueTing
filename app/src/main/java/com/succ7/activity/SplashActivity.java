package com.succ7.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;

import com.succ7.yueting.R;

import java.lang.ref.WeakReference;

/**
 * 欢迎页面
 *
 * @author zhongcj(QQ : 527633405) 2015-8-10
 */
public class SplashActivity extends FragmentActivity {

    private MyHandler mHandler = new MyHandler(this);
    private final int mDelayMillis = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        // 欢迎页面停留两秒钟
        mHandler.sendEmptyMessageDelayed(0, mDelayMillis);

    }

    @Override
    public void onBackPressed() {
        // 什么也不做,在欢迎界面禁止用户回退
    }

    private static class MyHandler extends Handler {
        // 使用弱引用,避免handler造成的内存泄露(message持有handler的引用,内部定义的handler类持有外部类的引用)
        WeakReference<SplashActivity> mFragmentWeakReference;
        SplashActivity mActivity;

        MyHandler(SplashActivity a) {
            mFragmentWeakReference = new WeakReference<>(a);
            mActivity = mFragmentWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            mActivity.startActivity(new Intent(mActivity, MainContentActivity.class));
            mActivity.finish();
        }
    }

}
