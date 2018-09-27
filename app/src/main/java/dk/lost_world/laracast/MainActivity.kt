package dk.lost_world.laracast

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.mikepenz.materialdrawer.DrawerBuilder
import dk.lost_world.laracast.WebClient.MyWebChromeClient
import dk.lost_world.laracast.WebClient.MyWebViewClient
import kotlinx.android.synthetic.main.activity_main.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import android.net.Proxy.getHost
import android.os.Handler
import android.view.View
import android.webkit.ValueCallback
import com.google.android.exoplayer2.ExoPlayerFactory
import dk.lost_world.laracast.WebClient.MyJavaScriptInterface
import java.net.MalformedURLException
import java.net.URL
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util


class MainActivity : AppCompatActivity() {

    lateinit var webView : WebView

    lateinit var fullscreenView : FrameLayout

    lateinit var player: SimpleExoPlayer

    var videoPlaying : Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar : Toolbar = findViewById(R.id.my_toolbar)

        setSupportActionBar(toolbar)

        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(

                )
                .build()

        webView = findViewById(R.id.webView)
        fullscreenView = findViewById(R.id.fullscreenView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(MyJavaScriptInterface(this), "VideoView")

        webView.webViewClient = MyWebViewClient(this)
        webView.webChromeClient = MyWebChromeClient(this, fullscreenView)

        //webView.loadUrl(getString(R.string.url_prefix) + getString(R.string.base_url))
        webView.loadUrl("https://laracasts.com/series/whats-new-in-laravel-5-7/episodes/1")

        // 1. Create a default TrackSelector
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)

        videoView.player = player
        videoView.keepScreenOn = true
        videoView.controllerAutoShow = true
        videoView.controllerHideOnTouch = true

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        webView.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        webView.restoreState(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchItem = menu.findItem(R.id.search)


        (searchItem.actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchItem.collapseActionView()

                    try {
                        val q = URLEncoder.encode(query, "UTF-8")
                        webView.loadUrl(getString(R.string.searchTemplateUrl) + q)
                    } catch (e: UnsupportedEncodingException) {
                        return true
                    }

                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    // do nothing
                    return true
                }
            })

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    searchItem.collapseActionView()
                }
            }
        }

        return true
    }

    fun playVideo(videoUri: String, cookies: String) {

        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultHttpDataSourceFactory(
                Util.getUserAgent(this, "LaraCastAndroid")
        )
        dataSourceFactory.defaultRequestProperties.set("Cookie", "$cookies\r\n")

        // This is the MediaSource representing the media to be played.
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoUri))
        // Prepare the player with the source.
        player.prepare(videoSource)

        videoView.visibility = View.VISIBLE
        webView.visibility = View.INVISIBLE

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    public override fun onUserLeaveHint() {
        if (videoPlaying) {
            enterPictureInPictureMode()
        }
    }
}
