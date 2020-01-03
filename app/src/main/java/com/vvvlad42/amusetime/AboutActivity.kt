package com.vvvlad42.amusetime

import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val htmlAsString = getString(R.string.aboutText) // used by WebView
        val webView = findViewById<WebView>(R.id.aboutTextView)
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null)
        configureToolbar()
    }


    private fun configureToolbar(){
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true) // switch on the left hand icon
            actionBar.setHomeAsUpIndicator(R.drawable.ic_play_loc_2) // replace with your custom icon
//            actionBar.setIcon(R.drawable.ic_play_loc_2)
            var t=5

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
//            val intent = Intent(this, MapsActivity::class.java)
////            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
