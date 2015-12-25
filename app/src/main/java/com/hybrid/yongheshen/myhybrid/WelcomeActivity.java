package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.os.Bundle;

import com.hybrid.yongheshen.myhybrid.util.InitFramwork;

/**
 * 作者： shenyonghe689 on 15/12/8.
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

