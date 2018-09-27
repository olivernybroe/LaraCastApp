package dk.lost_world.laracast.WebClient


import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import dk.lost_world.laracast.MainActivity
import dk.lost_world.laracast.R

internal class MyWebViewClient(var activity: MainActivity) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {

        activity.webView.evaluateJavascript(
                "(function() { return \$(\"a[title='Download Video']\").prop('href'); })();"
        ) {
            val cookies = CookieManager.getInstance().getCookie(url)
            val videoUrl = it.drop(1).dropLast(1)

            activity.playVideo(videoUrl, cookies)
        }


        super.onPageFinished(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        Log.d("URL", request?.url.toString())
        if(request?.url.toString() == activity.getString(R.string.base_url)) {
            // This is my web site, so do not override; let my WebView load the page
            return false
        }

        return super.shouldOverrideUrlLoading(view, request)
    }


}