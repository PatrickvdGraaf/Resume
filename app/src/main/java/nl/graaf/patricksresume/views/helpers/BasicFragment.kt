package nl.graaf.patricksresume.views.helpers


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.resume.ResumeFragment


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ResumeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
open class BasicFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val textView = TextView(activity)
        textView.setText(R.string.hello_blank_fragment)
        return textView
    }

}// Required empty public constructor
