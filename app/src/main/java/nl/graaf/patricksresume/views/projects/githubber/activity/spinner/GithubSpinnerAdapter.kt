package nl.graaf.patricksresume.views.projects.githubber.activity.spinner

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.projects.githubber.models.GithubRepo

/**
 * Created by patrick on 12/5/17.
 * 11:46 AM, Notive B.V.
 *
 * © Copyright 2017
 */
class GithubSpinnerAdapter(context: Activity, resource: Int, objects: List<GithubRepo>)
    : ArrayAdapter<GithubRepo>(context, resource, objects) {

    private val mContext: Activity = context
    private val mData: List<GithubRepo> = objects

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row = convertView
        if (row == null) {
            val inflater: LayoutInflater = mContext.layoutInflater
            row = inflater.inflate(R.layout.spinner_githubrepo_dropdown_item, parent,
                    false)
        }
        val textViewName: TextView = row!!.findViewById(R.id.textViewRepoName)
        val textViewOwner: TextView = row.findViewById(R.id.textViewRepoOwner)

        val repo: GithubRepo = mData[position]
        textViewName.text = repo.getName()
        textViewOwner.text = repo.getOwner()

        return row
    }
}