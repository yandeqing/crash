package com.ydq.crash;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        new Thread() {
            @Override
            public void run() {
                super.run();
                EmailerSDK.sendTextByEmail(getApplication(), "这是一个邮件发送测试");
            }
        }.start();
    }
}