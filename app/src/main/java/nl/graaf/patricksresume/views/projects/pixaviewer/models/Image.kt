package nl.graaf.patricksresume.views.projects.pixaviewer.models

import com.google.gson.JsonObject
import org.json.JSONException

/**
 * Created by patrick on 12/5/17.
 * 1:06 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class Image {
    companion object {
        @Throws(JSONException::class)
        fun fromJson(jsonObject: JsonObject): Image {
            val image = Image()
            image.id = jsonObject.get("id").asInt
            image.pageUrl = jsonObject.get("pageURL").asString
            image.type = jsonObject.get("type").asString
            image.previewURL = jsonObject.get("previewURL").asString
            image.previewWidth = jsonObject.get("previewWidth").asInt
            image.previewHeight = jsonObject.get("previewHeight").asInt
            image.webformatURL = jsonObject.get("webformatURL").asString
            image.webformatWidth = jsonObject.get("webformatWidth").asInt
            image.webformatHeight = jsonObject.get("webformatHeight").asInt
            image.imageWidth = jsonObject.get("imageWidth").asInt
            image.imageHeight = jsonObject.get("imageHeight").asInt
            image.downloads = jsonObject.get("downloads").asInt
            image.favourites = jsonObject.get("favorites").asInt
            image.likes = jsonObject.get("likes").asInt
            image.comments = jsonObject.get("comments").asInt
            image.user_id = jsonObject.get("user_id").asInt
            image.user = jsonObject.get("user").asString
            image.userImageUrl = jsonObject.get("userImageURL").asString

            val tagsArray: String = jsonObject.get("tags").asString
            image.tags = tagsArray.split(",")

            return image
        }
    }

    var id: Int = 0
    lateinit var pageUrl: String
    lateinit var type: String
    lateinit var tags: List<String>
    lateinit var previewURL: String
    var previewWidth: Int = 0
    var previewHeight: Int = 0
    lateinit var webformatURL: String
    var webformatWidth: Int = 0
    var webformatHeight: Int = 0
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    var views: Int = 0
    var downloads: Int = 0
    var favourites: Int = 0
    var likes: Int = 0
    var comments: Int = 0
    var user_id: Int = 0
    var user: String = ""
    var userImageUrl: String = ""
    private var mRgb: Int = 0
    private var mPrimaryTextColor = 0

    fun setRGB(rgb: Int) {
        mRgb = rgb
    }

    fun getRGB(): Int {
        return mRgb
    }

    fun setPrimaryTextColor(textColor: Int) {
        mPrimaryTextColor = textColor
    }

    fun getPrimaryTextColor(): Int {
        return mPrimaryTextColor
    }
}