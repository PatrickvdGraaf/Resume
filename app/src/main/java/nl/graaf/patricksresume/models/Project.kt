package nl.graaf.patricksresume.models

import android.content.Context
import android.content.Intent
import org.json.JSONException
import org.json.JSONObject


/**
 *
 * Created by Patrick van de Graaf on 11/25/2017.
 *
 */
class Project {
    private var mIcon: Int = 0
    private lateinit var mName: String
    private lateinit var mDesc: String
    private lateinit var mStartIntentName: String

    @Throws(ClassNotFoundException::class)
    fun getStartIntent(context: Context): Intent {
        val c = Class.forName(mStartIntentName)
        return Intent(context, c)
    }

    fun getProjectName(): String {
        return mName
    }

    fun getProjectDescription(): String {
        return mDesc
    }

    fun getProjectIcon(): Int {
        return mIcon
    }

    companion object {
        @Throws(JSONException::class)
        fun fromJson(context: Context, jsonObject: JSONObject): Project {
            val project = Project()
            project.mName = getStringFor(jsonObject.getString("app_name"), context)
            project.mDesc = getStringFor(jsonObject.getString("app_desc"), context)
            project.mIcon = getImageFor(jsonObject.getString("app_icon"), context)
            project.mStartIntentName = jsonObject.getString("app_activity")

            return project
        }

        fun fromProperties(context: Context, projectJson: JSONObject, index: Int): Project {
            val project = Project()
            project.mName = getStringFor(projectJson.getJSONArray("app_name")
                    .getString(index), context)
            project.mDesc = getStringFor(projectJson.getJSONArray("app_desc")
                    .getString(index), context)
            project.mIcon = getImageFor(projectJson.getJSONArray("app_icon")
                    .getString(index), context)
            project.mStartIntentName = projectJson.getJSONArray("app_activity")
                    .getString(index)
            return project
        }


        private fun getStringFor(name: String, context: Context): String {
            return context.getString(context.resources.getIdentifier(name, "string",
                    context.packageName))
        }

        private fun getImageFor(name: String, context: Context): Int {
            var imgRes = context.resources.getIdentifier(name, "mipmap",
                    context.packageName)
            if (imgRes <= 0) {
                imgRes = context.resources.getIdentifier(name, "drawable",
                        context.packageName)
            }
            return imgRes
        }
    }


}