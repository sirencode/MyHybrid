package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;

import com.hybrid.yongheshen.myhybrid.web.BaseWebView;
import com.hybrid.yongheshen.myhybrid.web.MyWebViewClient;

public class MainActivity extends Activity implements View.OnClickListener
{
    BaseWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews()
    {
        Button callJs = (Button) findViewById(R.id.btn_callJs);
        callJs.setOnClickListener(this);
        Button httpGet = (Button) findViewById(R.id.btn_httpGet);
        httpGet.setOnClickListener(this);
        Button httpPost = (Button) findViewById(R.id.btn_httpPost);
        httpPost.setOnClickListener(this);
        mWebView = (BaseWebView) findViewById(R.id.webView);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(MainActivity.this), "jsInterface");
        mWebView.loadUrl(MyApplication.BASE_URL);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_callJs:
                mWebView.loadUrl("javascript:testAlert()");
                break;

            case R.id.btn_httpGet:

                break;

            default:
                break;
        }

    }

}
