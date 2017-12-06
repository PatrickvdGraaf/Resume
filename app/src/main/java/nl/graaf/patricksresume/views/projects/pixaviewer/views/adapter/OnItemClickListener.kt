package nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter

import android.view.View

/**
 * Created by patrick on 12/6/17.
 * 1:09 PM, Notive B.V.
 *
 * © Copyright 2017
 */
interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
    fun onLongItemClick(view: View, position: Int)
}