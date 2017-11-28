package nl.graaf.patricksresume.models

import android.content.Intent

/**
 *
 * Created by Patrick van de Graaf on 11/25/2017.
 *
 */
class Project(val icon : Int, val name : String, val description : String,
              private val startIntent: Intent) {
    fun getStartIntent(): Intent = startIntent
}