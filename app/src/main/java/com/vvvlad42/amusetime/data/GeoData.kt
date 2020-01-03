package com.vvvlad42.amusetime.data

import java.util.*


data class PlaceLocation(
    val type: String,
    val lat: Double,
    val lng: Double,
    val name_he: String,
    val name_en: String
    ){
    constructor(
        id: Int,
        node_id :String,
        type: String,
        lat: Double,
        lng: Double,
        coordinates_list: List<Any>,
        name_he: String,
        name_en: String,
        created_by:Int,
        updated_by:Int,
        update_date:Date
    ):this(type, lat, lng, name_he, name_en)
}
















//data class Overpass(
//        val type: String,
//        val generator: String,
//        val copyright: String,
//        val timestamp: String,
//        val features: List<Feature>
//    )
//
//    data class Feature(
//        val geometry: Geometry?,
//        val id: String?,
//        val properties: Properties?,
//        val type: String?
//    )
//    data class Properties(
//        @SerializedName("@id")
//        val id: String?,
//        val leisure: String?
//    )
//    data class Geometry(
//        //types can be Ploygon, Point, maybe something else
//        val type: String?,
//        //for point it is just two doubles, but for Polygon it is [[[][]]] json structure
//        val coordinates: List<Any>?
//
//    )