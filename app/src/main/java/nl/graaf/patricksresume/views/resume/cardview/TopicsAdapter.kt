package nl.graaf.patricksresume.views.resume.cardview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView



/**
 *
 * Created by Patrick van de Graaf on 11/23/2017.
 *
 */

class TopicsAdapter : RecyclerView.Adapter<TopicsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(// each data item is just a string in this case
            var mTextView: TextView) : RecyclerView.ViewHolder(mTextView)

    // Provide a suitable constructor (depends on the kind of dataset)
    fun TopicsAdapter(){
    }



}