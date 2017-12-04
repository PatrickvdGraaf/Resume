package nl.graaf.patricksresume.views.apps

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.models.Project
import nl.graaf.patricksresume.views.apps.ProjectFragment.OnListFragmentInteractionListener

/**
 * [RecyclerView.Adapter] that can display a [Project] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyProjectRecyclerViewAdapter(private val mListener: ProjectsRecyclerViewListener)
    : RecyclerView.Adapter<MyProjectRecyclerViewAdapter.ViewHolder>() {

    private var mInteractionListener: OnListFragmentInteractionListener? = null
    private val mValues: ArrayList<Project> = ArrayList()

    interface ProjectsRecyclerViewListener {
        fun getParentActivity(): Activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cardview_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setProject(mListener.getParentActivity(), mValues[position])
        holder.mView.setOnClickListener {
            mInteractionListener?.onListFragmentInteraction(mListener.getParentActivity(),
                    mValues[position], holder.getTransitionView())
        }
    }

    fun setListener(interactionListener: OnListFragmentInteractionListener) {
        mInteractionListener = interactionListener
    }

    fun addItem(project: Project) {
        if (!mValues.contains(project)) {
            mValues.add(project)
            notifyItemInserted(itemCount - 1)
        }
    }

    fun removeAllProjects() {
        while (itemCount != 0) {
            val i = itemCount
            mValues.removeAt(i - 1)
            notifyItemRemoved(i - 1)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        private val mTitleView = mView.findViewById<View>(R.id.textViewProjectTitle) as TextView
        private val mDescView = mView.findViewById<View>(R.id.textViewProjectDesc) as TextView
        private val mIconView = mView.findViewById<View>(R.id.imageViewProjectIcon) as ImageView
        private var mItem: Project? = null

        fun setProject(context: Context, project: Project) {
            mItem = project
            mTitleView.text = project.getProjectName()
            mDescView.text = project.getProjectDescription()
            Glide.with(context)
                    .load(project.getProjectIcon())
                    .into(mIconView)
        }

        fun getTransitionView(): View {
            return mIconView
        }

        override fun toString(): String = super.toString() + " '" + mTitleView.text + "'"
    }
}
