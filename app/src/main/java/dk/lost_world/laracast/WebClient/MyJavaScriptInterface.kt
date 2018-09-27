package dk.lost_world.laracast.WebClient

import android.content.Context
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AlertDialog


class MyJavaScriptInterface(private val ctx: Context) {

    @JavascriptInterface
    fun showHTML(html: String) {
        AlertDialog.Builder(ctx).setTitle("HTML").setMessage(html)
                .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show()
    }

}