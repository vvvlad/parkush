package com.vvvlad42.amusetime

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vvvlad42.amusetime.data.LocationParcel
import kotlinx.android.synthetic.main.activity_share_location.*

class ShareLocation : AppCompatActivity() {

    lateinit var sharedLocation:LocationParcel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_location)
        val htmlAsString = getString(R.string.shareText) // used by WebView
        val webView = findViewById<WebView>(R.id.shareTextView)
        webView.loadDataWithBaseURL(null, htmlAsString, "text/html", "utf-8", null)
        configureToolbar()



        btnReportLocation.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                sharedLocation = intent.getParcelableExtra("SharedLocation")
                val msg:String = "שלום, "+ System.lineSeparator()+ "יש הפַּארְקוּש כאן: "+ System.lineSeparator() + sharedLocation.lat + " : "+sharedLocation.lng
                sendEmail("vvvlad42@gmail.com", "שיתוף הפַּארְקוּש חדש", msg)
            }
        })



    }
    private fun sendEmail(recipient: String, subject: String, message: String) {
        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val emailSelectorIntent = Intent(Intent.ACTION_SENDTO)
        emailSelectorIntent.data = Uri.parse("mailto:")

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        emailIntent.selector = emailSelectorIntent

        try {
            //start email intent
            startActivity(Intent.createChooser(emailIntent, "Choose Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }

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
