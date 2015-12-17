package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
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
        InitFramwork initFramwork = new InitFramwork(WelcomeActivity.this);
        initFramwork.init();
    }

}

