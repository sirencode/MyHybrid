package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 作者： yongheshen on 15/12/8.
 * 描述：
 */
public class WelcomeActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        InitFramwork initFramwork = new InitFramwork(getApplicationContext());
        initFramwork.setInitDoneInterface(new InitFramwork.OnInitDoneInterface()
        {

            @Override
            public void doneInit()
            {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        });
        initFramwork.init();
    }

}

