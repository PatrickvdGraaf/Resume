package nl.graaf.patricksresume.views.projects.pixaviewer.network.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import nl.graaf.patricksresume.views.projects.pixaviewer.network.ImageResponse
import java.lang.reflect.Type

/**
 * Created by patrick on 12/5/17.
 * 1:14 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class ImageResponseDeserializer : JsonDeserializer<ImageResponse> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?,
                             context: JsonDeserializationContext?): ImageResponse {

        return ImageResponse.fromJson(json?.asJsonObject)
    }
}