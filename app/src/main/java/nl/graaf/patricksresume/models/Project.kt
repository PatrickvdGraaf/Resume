package nl.graaf.patricksresume.models

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber


/**
 *
 * Created by Patrick van de Graaf on 11/25/2017.
 *
 */
class Project {
    private var mIcon: Int = 0
    private var mName: String = ""
    private var mDesc: String = ""
    private var mStartIntentName: String = ""

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
            val appName = jsonObject.getString("app_name")
            try {
                Timber.d(String.format("project_%s_title", appName))
                project.mName = getStringFor(String.format("project_%s_title", appName), context)
                project.mDesc = getStringFor(String.format("project_%s_desc", appName), context)
                project.mIcon = getImageFor(String.format("ic_launcher_%s", appName), context)
                project.mStartIntentName = jsonObject.getString("app_activity")
            } catch (ex: Resources.NotFoundException) {
                Timber.e(ex, String.format("Project could not load resources for appName: %s",
                        appName))
            }

            return project
        }

        fun fromProperties(context: Context, projectJson: JSONObject, index: Int): Project {
            val project = Project()
            val appName = projectJson.getJSONArray("app_name").getString(index)
            try {
                project.mName = getStringFor(String.format("project_%s_title", appName), context)
                project.mDesc = getStringFor(String.format("project_%s_desc", appName), context)
                project.mIcon = getImageFor(String.format("ic_launcher_%s", appName), context)
                project.mStartIntentName = projectJson.getJSONArray("app_activity")
                        .getString(index)
            } catch (ex: Resources.NotFoundException) {
                Timber.e(ex, String.format("Project could not load resources for appName: %s",
                        appName))
            }

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