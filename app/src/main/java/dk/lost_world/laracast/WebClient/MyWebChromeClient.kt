package dk.lost_world.laracast.WebClient

import android.content.pm.ActivityInfo
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import dk.lost_world.laracast.MainActivity


class MyWebChromeClient(var activity: MainActivity, var fullscreenView: FrameLayout) : WebChromeClient() {

    override fun onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
        super.onShowCustomView(view, callback)

        activity.webView.visibility = View.GONE
        fullscreenView.visibility = View.VISIBLE
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        activity.videoPlaying = true

        hideSystemUI()

        fullscreenView.addView(view)
    }

    override fun onHideCustomView() {
        super.onHideCustomView()

        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        activity.webView.visibility = View.VISIBLE
        fullscreenView.visibility = View.GONE

        activity.videoPlaying = false

        showSystemUI()
    }

    private fun hideSystemUI() {
        activity.supportActionBar?.hide()
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        activity.supportActionBar?.show()
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

}