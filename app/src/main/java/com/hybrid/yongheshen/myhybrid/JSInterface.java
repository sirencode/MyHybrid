package com.hybrid.yongheshen.myhybrid;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * 作者： yongheshen on 15/12/7.
 * 描述：
 */
class JSInterface implements WebViewInterface{

    private Context mContext;

    public  JSInterface(Context context)
    {
        this.mContext = context;
    }

    @JavascriptInterface
    public String onToast(String text) {
        final String str = text;
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.e("leehong2", "onToast: text = " + str);
//                Toast.makeText(mContext, "onToast = " + str, Toast.LENGTH_LONG).show();
            }
        }).start();

        return "This text is returned from Java layer.  js text = " + text;
    }

}
