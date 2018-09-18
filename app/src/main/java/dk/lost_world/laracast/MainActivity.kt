package dk.lost_world.laracast

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.webkit.WebView
import android.webkit.WebSettings
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.appcompat.widget.Toolbar
import com.mikepenz.iconics.utils.Utils
import com.mikepenz.materialdrawer.DrawerBuilder
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    lateinit var webView : WebView

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

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = MyWebViewClient(this)

        webView.loadUrl(getString(R.string.url_prefix) + getString(R.string.base_url))


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
}
