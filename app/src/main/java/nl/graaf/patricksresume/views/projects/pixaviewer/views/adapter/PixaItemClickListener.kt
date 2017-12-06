package nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by patrick on 12/6/17.
 * 1:08 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class PixaItemClickListener(context: Context, recyclerView: RecyclerView,
                            listener: OnItemClickListener) : RecyclerView.OnItemTouchListener {
    private var mListener: OnItemClickListener = listener
    private var mGestureDetector: GestureDetector = GestureDetector(context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent) {
                    super.onLongPress(e)
                    val childView: View? = recyclerView.findChildViewUnder(e.x, e.y)
                    if (childView != null) {
                        mListener.onLongItemClick(childView,
                                recyclerView.getChildAdapterPosition(childView))
                    }
                }
            })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView: View? = view.findChildViewUnder(e.x, e.y)
        if (childView != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView))
            return true
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }

    override fun onTouchEvent(rv: RecyclerView?, e: MotionEvent?) {
    }
}