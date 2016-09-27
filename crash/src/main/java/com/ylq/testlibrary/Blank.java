package com.ylq.testlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by apple on 16/7/8.
 */
public class Blank extends LinearLayout {
    private int count = 0;
    private Button button;

    public Blank(Context context) {
        super(context);
        init(context);
    }

    public Blank(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Blank(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Blank(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }


    private void init(final Context context) {
        LayoutInflater.from(context).inflate(R.layout.button, this);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;

                button.setText(count+"");
            }
        });
    }

}
