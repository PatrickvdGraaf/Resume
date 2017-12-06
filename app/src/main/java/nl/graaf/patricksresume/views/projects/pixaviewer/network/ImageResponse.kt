package nl.graaf.patricksresume.views.projects.pixaviewer.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import nl.graaf.patricksresume.views.projects.pixaviewer.models.Image
import org.json.JSONException

/**
 * Created by patrick on 12/5/17.
 * 1:05 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class ImageResponse {
    companion object {
        @Throws(JSONException::class)
        fun fromJson(jsonObject: JsonObject?): ImageResponse {
            val imageResponse = ImageResponse()
            if (jsonObject != null) {
                imageResponse.mTotal = jsonObject.get("total").asInt
                imageResponse.mTotalHits = jsonObject.get("totalHits").asInt
                imageResponse.mHits = ArrayList()

                val jsonHitsArray: JsonArray = jsonObject.getAsJsonArray("hits")
                (0..(jsonHitsArray.size() - 1))
                        .map { jsonHitsArray.get(it) }
                        .forEach { imageResponse.mHits.add(Image.fromJson(it.asJsonObject)) }
            }


            return imageResponse
        }
    }

    @SerializedName("total")
    var mTotal: Int = 0
    @SerializedName("totalHits")
    var mTotalHits: Int = 0
    @SerializedName("hits")
    private lateinit var mHits: ArrayList<Image>

    fun getHits(): ArrayList<Image> {
        return mHits
    }
}