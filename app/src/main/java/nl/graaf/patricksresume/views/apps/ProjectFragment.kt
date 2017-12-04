package nl.graaf.patricksresume.views.apps

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import nl.graaf.patricksresume.BuildConfig
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.models.Project
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
class ProjectFragment : Fragment(), MyProjectRecyclerViewAdapter.ProjectsRecyclerViewListener {
    companion object {
        val ANIM_OPEN_DURATION: Long = 500
        val ANIM_ADD_DURATION: Long = 100
        val ANIM_DELAY: Float = 0.2f
        fun newInstance(): Fragment = ProjectFragment()
    }

    private var mListener: OnListFragmentInteractionListener? = null
    private val mProjectsAdapter = MyProjectRecyclerViewAdapter(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            view.setHasFixedSize(true)
            view.layoutManager = LinearLayoutManager(activity.applicationContext)
            view.adapter = mProjectsAdapter
            setListItemAnimation(view)


            if (BuildConfig.DEBUG) {
                //Test normal JSON Array vs Struct of Arrays performance (Huawei p8, 4-12-17)
                //Latest recorded test with 8 projects:
                //
                //getProjects() = 8627291 nanoSeconds
                //getProjectsFromStruct() = 3550000 nanoSeconds
                //
                //Result: Gained 10346875 nanoSeconds by using Struct.
                //
                //Note: using rxKotlin on the struct method reduced the performance significantly.

                val startTime1 = System.nanoTime()
                getProjects()
                val endTime1 = System.nanoTime()
                val normalDuration = (endTime1 - startTime1)

                mProjectsAdapter.removeAllProjects()

                val startTime2 = System.nanoTime()
                getProjectsFromStruct()
                val endTime2 = System.nanoTime()
                val structDuration = (endTime2 - startTime2)

                Timber.d("Normal JSON serialization duration: %s", normalDuration)
                Timber.d("Struct of Array serialization duration: %s", structDuration)
            } else {
                getProjectsFromStruct()
            }
        }
        return view
    }

    private fun getProjects() {
        val jsonArray: JSONArray = loadJSONFromAsset()

        var i = 0
        while (i != jsonArray.length()) {
            mProjectsAdapter.addItem(Project.fromJson(activity, jsonArray.getJSONObject(i)))
            i++
        }
    }

    private fun getProjectsFromStruct() {
        val projectsObject: JSONObject = loadJSONFromStruct()

        var i = 0
        while (i != projectsObject.getJSONArray("app_name").length()) {
            mProjectsAdapter.addItem(Project.fromProperties(activity, projectsObject, i))
            i++
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
            mProjectsAdapter.setListener(mListener!!)
        } else {
            throw RuntimeException(context!!.toString()
                    + " must implement OnListFragmentInteractionListener")
        }
    }

    private fun loadJSONFromAsset(): JSONArray {
        return try {
            val `is` = activity.assets.open("projects.json")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val jsonString = String(buffer, Charset.forName("UTF-8"))
            JSONArray(jsonString)
        } catch (ex: IOException) {
            Timber.e(ex, "Failed to parse projects JSON")
            JSONArray()
        }
    }

    private fun loadJSONFromStruct(): JSONObject {
        return try {
            val `is` = activity.assets.open("projects_struct")
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val jsonString = String(buffer, Charset.forName("UTF-8"))
            JSONObject(jsonString).getJSONObject("projects")
        } catch (ex: IOException) {
            Timber.e(ex, "Failed to parse projects JSON")
            JSONObject()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun setListItemAnimation(view: RecyclerView) {
        val set = AnimationSet(true)

        var animation: Animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = ANIM_OPEN_DURATION
        set.addAnimation(animation)

        animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0.0f
        )
        animation.setDuration(ANIM_ADD_DURATION)
        set.addAnimation(animation)

        val controller = LayoutAnimationController(set, ANIM_DELAY)
        view.layoutAnimation = controller
    }

    override fun getParentActivity(): Activity {
        return this.activity
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(activity: Activity, project: Project, interactionView: View) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    interactionView,
                    activity.applicationContext.getString(R.string.transition_open_app_name))
            activity.startActivity(project.getStartIntent(activity), options.toBundle())
        }
    }
}
