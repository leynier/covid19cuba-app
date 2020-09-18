package club.postdata.covid19cuba

import android.content.Context
import org.json.JSONObject
import org.mapsforge.core.model.LatLong

class DataModel() {
    val days = LinkedHashMap<String, ArrayList<LatLong>>()

    fun getCoordinates(): List<LatLong> {
        val result = ArrayList<LatLong>()
        for (item in days) {
            result.addAll(item.component2())
        }
        return result
    }

    fun getDays(): List<String> {
        val result = ArrayList<String>()
        for (item in days) {
            result.add(item.component1())
        }
        return result
    }

    constructor(text: String) : this() {
        var zones: JSONObject? = null
        val json = JSONObject(text)
        if (json.has("all")) {
            val all = json.getJSONObject("all")
            if (all.has("zones")) {
                zones = all.getJSONObject("zones")
            }
        } else if (json.has("zones")) {
            zones = json.getJSONObject("zones")
        }
        if (zones != null) {
            val keys = zones.keys()
            for (key in keys) {
                days[key] = ArrayList()
                val array = zones.getJSONArray(key)
                for (i in 0 until array.length()) {
                    val list = array.getJSONArray(i)
                    val lat = list.getDouble(0)
                    val lon = list.getDouble(1)
                    days[key]?.add(LatLong(lat, lon))
                }
            }
        }
    }

    companion object {
        fun load(context: Context): DataModel {
            val preferences = context.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)
            val all = preferences.all;
            for (item in all) {
                print(item.key)
            }
            val json = preferences.getString("flutter.data", null) ?: return DataModel()
            return DataModel(json)
        }
    }
}
