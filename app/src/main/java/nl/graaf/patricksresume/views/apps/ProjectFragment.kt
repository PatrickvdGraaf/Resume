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
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.models.Project
import nl.graaf.patricksresume.views.projects.dicee.launcher.DiceeLauncherActivity
import nl.graaf.patricksresume.views.projects.magic8ball.Magic8BallActivity
import nl.graaf.patricksresume.views.projects.xylophone.XylophoneActivity


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
    override fun getParentActivity(): Activity {
        return this.activity
    }

    private var mListener: OnListFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_project_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = MyProjectRecyclerViewAdapter(this, getProjects(), mListener)
        }
        return view
    }

    private fun getProjects(): ArrayList<Project> {
        val list = ArrayList<Project>()
        list.add(Project(R.drawable.ic_dicee, getString(R.string.project_dicee_title),
                getString(R.string.project_dicee_desc),
                DiceeLauncherActivity.getStartIntent(activity!!.applicationContext)))
        list.add(Project(R.drawable.ic_xylophone, getString(R.string.project_xylophone_title),
                getString(R.string.project_xylophone_desc),
                XylophoneActivity.getStartIntent(activity!!.applicationContext)))
        list.add(Project(R.drawable.ic_eight_ball, getString(R.string.project_eight_ball_title),
                getString(R.string.project_eight_ball_desc),
                Magic8BallActivity.getStartIntent(activity!!.applicationContext)))
        return list
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString()
                    + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
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
            //TODO change to ActivityOptions?
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    interactionView,
                    activity.applicationContext.getString(R.string.transition_open_app_name))
            activity.startActivity(project.getStartIntent(), options.toBundle())
        }
    }

    companion object {
        fun newInstance(): Fragment = ProjectFragment()
    }
}
