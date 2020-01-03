package com.vvvlad42.amusetime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.vvvlad42.amusetime.data.DatabaseHelper

class UpdateReceiver : BroadcastReceiver() {

    val TAG = "UpdateReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "App got updated!")
        if ("android.intent.action.MY_PACKAGE_REPLACED" == intent?.action)
        {
            val dbHelper= DatabaseHelper(context)
            dbHelper.doDbCopyReplace()
        }

    }

}