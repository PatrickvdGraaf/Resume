package nl.graaf.patricksresume.views.apps

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.models.Project
import nl.graaf.patricksresume.views.apps.ProjectFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Project] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyProjectRecyclerViewAdapter(private val mListener: ProjectsRecyclerViewListener,
                                   private val mValues: ArrayList<Project>,
                                   private val mInteractionListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyProjectRecyclerViewAdapter.ViewHolder>() {

    interface ProjectsRecyclerViewListener {
        fun getParentActivity(): Activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setProject(mValues[position])
        holder.mView.setOnClickListener {
            mInteractionListener?.onListFragmentInteraction(mListener.getParentActivity(),
                    mValues[position], holder.getTransitionView())
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mTitleView = mView.findViewById<View>(R.id.textViewProjectTitle) as TextView
        private val mDescView = mView.findViewById<View>(R.id.textViewProjectDesc) as TextView
        private val mIconView = mView.findViewById<View>(R.id.imageViewProjectIcon) as ImageView
        private var mItem: Project? = null

        fun setProject(project: Project) {
            mItem = project
            mTitleView.text = project.getProjectName()
            mDescView.text = project.getProjectDescription()
            mIconView.setImageResource(project.getProjectIcon())
        }

        fun getTransitionView(): View {
            return mIconView
        }

        override fun toString(): String = super.toString() + " '" + mTitleView.text + "'"
    }
}
