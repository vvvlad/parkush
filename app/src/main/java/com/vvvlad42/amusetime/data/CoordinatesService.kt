package com.vvvlad42.amusetime.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.google.android.gms.maps.model.LatLng


class CoordinatesService(val context: Context) {
    private val TAG: String = "CoordinatesService"
    private var db: SQLiteDatabase

    init {
        val adb = DatabaseHelper(context)
        db = adb.openDatabase()
    }

    fun readLocations(bottom_left: LatLng, top_right: LatLng): List<PlaceLocation> {
        val locations = ArrayList<PlaceLocation>()
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(
                "select * from Location WHERE lat>" +
                        bottom_left.latitude + " AND lat<" + top_right.latitude + " AND lng>" +
                        bottom_left.longitude + " AND lng<" + top_right.longitude, null
            )
//            cursor = db.rawQuery(
//                "select * from Location", null
//            )
        } catch (e: SQLiteException) {
            return ArrayList()
        }

        var type:String
        var lat: Double
        var lng: Double
//        var coo_list: List<Any>
        var nameHe: String
        var nameEn: String
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                type = cursor.getString(cursor.getColumnIndex("type"))
                lat = cursor.getDouble(cursor.getColumnIndex("lat"))
                lng = cursor.getDouble(cursor.getColumnIndex("lng"))
                nameHe = cursor.getString(cursor.getColumnIndex("name_he"))
                nameEn = cursor.getString(cursor.getColumnIndex("name_en"))
                val pl = PlaceLocation(type, lat, lng, nameHe, nameEn)
                locations.add(pl)
//                locations.add(GeoData.kt.Location(id, node_id, null, null))
                cursor.moveToNext()
            }
        }
        cursor.close()
        return locations

    }
}




















//    fun readLocations(bottom_left: LatLng, top_right: LatLng): List<GeoData.kt.Location> {
//        val locations = ArrayList<GeoData.kt.Location>()
//        val db = writableDatabase
//        var cursor: Cursor?
//        try {
//            cursor = db.rawQuery("select * from Location WHERE lat>"+
//                    bottom_left.latitude + " AND lat<"+ top_right.latitude+" AND lng>"+
//                    bottom_left.longitude+ " AND lng<"+top_right.longitude  , null)
//        } catch (e: SQLiteException) {
//            return ArrayList()
//        }
//
//        var id: Int
//        var node_id: String
//        if (cursor!!.moveToFirst()) {
//            while (cursor.isAfterLast == false) {
//                id = cursor.getInt(cursor.getColumnIndex("id"))
//                node_id = cursor.getString(cursor.getColumnIndex("node_id"))
//
////                locations.add(GeoData.kt.Location(id, node_id, null, null))
//                cursor.moveToNext()
//            }
//        }
//        return locations
//    }



//    private fun readJsonFile(): String?
//    {
//        try {
//            val inputStream: InputStream = context.assets.open("export.geojson")
//            val inputStreamReader = InputStreamReader(inputStream)
//            val sb = StringBuilder()
//            var line: String?
//            val br = BufferedReader(inputStreamReader)
//
//            val inputString = br.use { it.readText() }
//            Log.d(TAG,sb.toString())
//            return inputString
//        } catch (e:Exception){
//            Log.d(TAG, e.toString())
//            return null
//        }
//    }
//    fun getCoordinatesFromJson(filter: String?):List<GeoData.kt.Feature>{
//        val gson = Gson()
//        val txtJson = readJsonFile()
//        val allFeatures:GeoData.kt.Overpass = gson.fromJson(txtJson, GeoData.kt.Overpass::class.java)
//        return allFeatures.features
//    }














