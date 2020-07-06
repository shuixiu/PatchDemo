package com.kotlin.kotlindemo.wvdemo

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import java.net.URL

class JavaScriptMethods {

    private var mContext: Context?=null

    private var mWebView: WebView?=null

    constructor(mContext: Context?,mWebView: WebView) {
        this.mContext = mContext
        this.mWebView = mWebView
    }


    @JavascriptInterface
    fun showToast(json:String){
        Toast.makeText(mContext,json,Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface
    fun getwanData(json: String){

        var jsondata = JSONObject(json)
        var callbackMethod = jsondata.opt("method")

        doAsync {
            var url = URL("https://wanandroid.com/wxarticle/chapters/json")
            val result = url.readText()

            mContext?.let {

                it.runOnUiThread {
                    mWebView?.let {
                        it.loadUrl("javascript:"+callbackMethod+"("+result+")")
                    }
                }
            }


        }
    }
}