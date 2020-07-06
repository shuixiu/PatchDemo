package com.kotlin.kotlindemo.wvdemo

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.kotlindemo.R
import kotlinx.android.synthetic.main.activity_webview.*
import org.json.JSONObject

class WebViewActivity :AppCompatActivity(){


    private val mWebview:WebView by lazy {
        wv_content
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_webview)


        setWebView()
    }

    //lambda

//    var add = {参数->返回值}

//    var add = {a:Int,b:Int-> a+b}

    var setWebView = {
        mWebview.settings.javaScriptEnabled = true;

        mWebview.webChromeClient = MyWebChromeClient()
        mWebview.webViewClient = MyWebViewClient()

        //js 调用kotlin
        mWebview.addJavascriptInterface(
            JavaScriptMethods(
                this,
                mWebview
            ),"jsInterface")

//        kotlin调用js

        mWebview.loadUrl("file:///android_asset/webview.html")
    }


    inner private class MyWebViewClient : WebViewClient(){
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            var json = JSONObject()
            json.put("name","kotlin")
            mWebview.loadUrl("javascript:showMessage("+json.toString()+")")
        }
    }

    private class MyWebChromeClient :WebChromeClient(){
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }
    }
}