package com.ydq.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ydq.crash.CrashCatchSDK;
import com.ydq.crash.EmailerSDK;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        ArrayList<String> receivers = new ArrayList<>();
                        receivers.add("546218945@qq.com");
                        EmailerSDK.setReceivers(receivers);
                        EmailerSDK.setAccoutPwd("1292234542@qq.com", "RPLfexaFoGXBaxiyZK9kCw==");
                        EmailerSDK.sendTextByEmail(MainActivity.this, "这是一个邮件发送测试");
                    }
                }.start();
            }
        });
        List<String> receivers = new ArrayList<>();
        receivers.add("546218945@qq.com");
        CrashCatchSDK.init(this, "1292234542@qq.com", "RPLfexaFoGXBaxiyZK9kCw==", receivers);
    }
}
