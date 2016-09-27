package com.ydq.customview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ydq.crash.CrashCatchSDK;
import com.ydq.crash.EmailerSDK;

import javax.mail.MessagingException;

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
                            try {
                                EmailerSDK.sendTextByEmail(MainActivity.this,"这是一个邮件发送测试");
                            } catch (MessagingException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
            }
        });
        CrashCatchSDK.init(this);

    }
}
