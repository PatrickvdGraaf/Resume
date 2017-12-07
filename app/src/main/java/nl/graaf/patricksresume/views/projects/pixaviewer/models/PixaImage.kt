package nl.graaf.patricksresume.views.projects.pixaviewer.models

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.graphics.Palette
import com.google.gson.JsonObject
import org.json.JSONException

/**
 * Created by patrick on 12/5/17.
 * 1:06 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class PixaImage() : Parcelable {
    companion object CREATOR : Parcelable.Creator<PixaImage> {
        const val BACKGROUND_ALPHA: Int = 64
        const val BACKGROUND_ALPHA_FLOAT: Float = BACKGROUND_ALPHA / 255f

        @Throws(JSONException::class)
        fun fromJson(jsonObject: JsonObject): PixaImage {
            val image = PixaImage()
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

        override fun createFromParcel(parcel: Parcel): PixaImage {
            return PixaImage(parcel)
        }

        override fun newArray(size: Int): Array<PixaImage?> {
            return arrayOfNulls(size)
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
    var titleTextColor: Int = Color.WHITE
    var bodyTextColor: Int = Color.WHITE
    var rgb: Int = Color.GRAY

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        pageUrl = parcel.readString()
        type = parcel.readString()
        tags = parcel.createStringArrayList()
        previewURL = parcel.readString()
        previewWidth = parcel.readInt()
        previewHeight = parcel.readInt()
        webformatURL = parcel.readString()
        webformatWidth = parcel.readInt()
        webformatHeight = parcel.readInt()
        imageWidth = parcel.readInt()
        imageHeight = parcel.readInt()
        views = parcel.readInt()
        downloads = parcel.readInt()
        favourites = parcel.readInt()
        likes = parcel.readInt()
        comments = parcel.readInt()
        user_id = parcel.readInt()
        user = parcel.readString()
        userImageUrl = parcel.readString()
        rgb = parcel.readInt()
        titleTextColor = parcel.readInt()
        bodyTextColor = parcel.readInt()
    }

    //TODO Separate into PixaImage and PixaImageView?
    //TODO Make functions to transform views into the style of the image (setTextView(textView))

    fun hasColors(): Boolean {
        return rgb != Color.GRAY
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(pageUrl)
        parcel.writeString(type)
        parcel.writeStringList(tags)
        parcel.writeString(previewURL)
        parcel.writeInt(previewWidth)
        parcel.writeInt(previewHeight)
        parcel.writeString(webformatURL)
        parcel.writeInt(webformatWidth)
        parcel.writeInt(webformatHeight)
        parcel.writeInt(imageWidth)
        parcel.writeInt(imageHeight)
        parcel.writeInt(views)
        parcel.writeInt(downloads)
        parcel.writeInt(favourites)
        parcel.writeInt(likes)
        parcel.writeInt(comments)
        parcel.writeInt(user_id)
        parcel.writeString(user)
        parcel.writeString(userImageUrl)
        parcel.writeInt(rgb)
        parcel.writeInt(titleTextColor)
        parcel.writeInt(bodyTextColor)
    }

    fun setPaletteValues(palette: Palette) {
        if (palette.dominantSwatch != null) {
            rgb = palette.dominantSwatch!!.rgb
            titleTextColor = palette.dominantSwatch!!.titleTextColor
            bodyTextColor = palette.dominantSwatch!!.bodyTextColor
        } else {
            for (swatch in palette.swatches) {
                if (swatch != null) {
                    rgb = swatch.rgb
                    titleTextColor = swatch.titleTextColor
                    bodyTextColor = swatch.bodyTextColor
                }
            }
        }
    }
}