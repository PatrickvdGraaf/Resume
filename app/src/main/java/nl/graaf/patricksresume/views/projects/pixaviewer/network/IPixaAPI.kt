package nl.graaf.patricksresume.views.projects.pixaviewer.network

import io.reactivex.Single
import retrofit2.http.GET

/**
 * Created by patrick on 12/5/17.
 * 12:30 PM, Notive B.V.
 *
 * © Copyright 2017
 */
interface IPixaAPI {
    companion object {
        val ENDPOINT = "https://pixabay.com/"
        val API_KEY = "7276840-dc7baf645e6eea3a8a395216b"
    }

    @GET("api/")
    fun getPictures(): Single<ImageResponse>
}