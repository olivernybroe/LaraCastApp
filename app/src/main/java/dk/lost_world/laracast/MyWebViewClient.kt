package dk.lost_world.laracast


import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

internal class MyWebViewClient(var activity: MainActivity) : WebViewClient() {



    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(request?.url.toString() == activity.getString(R.string.base_url)) {
            // This is my web site, so do not override; let my WebView load the page
            return false
        }

        return super.shouldOverrideUrlLoading(view, request)
    }
}