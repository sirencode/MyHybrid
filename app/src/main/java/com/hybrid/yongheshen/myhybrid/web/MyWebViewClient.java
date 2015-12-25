package com.hybrid.yongheshen.myhybrid.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hybrid.yongheshen.myhybrid.MyApplication;

/**
 * 作者： yongheshen on 15/12/14.
 */
public class MyWebViewClient extends WebViewClient
{
    /**
     * 在点击请求的是链接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边。
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        view.loadUrl(url);
        return true;
    }

    /**
     * 重写此方法可以让webview处理https请求
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
    {
        handler.proceed();
    }

    /**
     * 甚至404页面
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        super.onReceivedError(view, errorCode, description, failingUrl);
        view.loadUrl(MyApplication.URL_404);
    }

    /**
     * 重写此方法才能够处理在浏览器中的按键事件
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event)
    {
        return super.shouldOverrideKeyEvent(view, event);
    }

    /**
     * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
     */
    @Override
    public void onLoadResource(WebView view, String url)
    {
        super.onLoadResource(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);
    }
}
