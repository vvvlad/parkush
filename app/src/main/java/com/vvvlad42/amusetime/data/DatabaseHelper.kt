package com.vvvlad42.amusetime.data

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DatabaseHelper(private val context: Context) {
    private val TAG: String = "DatabaseHelper"
    companion object {
        private val DB_NAME = "locdb.sqlite"
    }

    fun doDbCopyReplace(){
        try {
            val delDb = context.deleteDatabase(DB_NAME)
            if (!delDb){
                Log.w(TAG, "Failed to delete db $DB_NAME")
            }
            val dbFile = context.getDatabasePath(DB_NAME)
            copyDatabase(dbFile)
            openDatabase()
        } catch (e: IOException) {
            throw RuntimeException("Error creating source database", e)
        }
    }

    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)
        if (!dbFile.exists()) {
            try {
                val checkDB = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
                checkDB?.close()
                copyDatabase(dbFile)
            } catch (e: IOException) {
                throw RuntimeException("Error creating source database", e)
            }

        }
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @SuppressLint("WrongConstant")
    private fun copyDatabase(dbFile: File) {
        val inptStream = context.assets.open(DB_NAME)
        val os = FileOutputStream(dbFile)

        val buffer = ByteArray(1024)
        while (inptStream.read(buffer) > 0) {
            os.write(buffer)
            Log.d("#DB", "writing>>")
        }

        os.flush()
        os.close()
        inptStream.close()
        Log.d("#DB", "completed..")
    }
}