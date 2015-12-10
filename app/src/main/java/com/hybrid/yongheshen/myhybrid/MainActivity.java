package com.hybrid.yongheshen.myhybrid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener
{
    WebViewEx mWebView;

    String mUrl = "file:///data/data/com.hybrid.yongheshen.myhybrid/core/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews()
    {
        Button callJs = (Button) findViewById(R.id.button);
        callJs.setOnClickListener(this);
        mWebView = (WebViewEx) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JSInterface(getApplicationContext()), "jsInterface");
        mWebView.setWebChromeClient(new WebChromeClient()
        {

        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v)
    {
        mWebView.loadUrl("javascript:testAlert()");
    }

}
