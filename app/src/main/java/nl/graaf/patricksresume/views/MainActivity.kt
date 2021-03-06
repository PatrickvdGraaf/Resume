package nl.graaf.patricksresume.views

import android.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.apps.ProjectFragment
import nl.graaf.patricksresume.views.helpers.BaseActivity
import nl.graaf.patricksresume.views.resume.ResumeFragment

class MainActivity : BaseActivity(), ProjectFragment.OnListFragmentInteractionListener {
    private val mOnNavigationItemSelectedListener
            = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
//                message.setText(R.string.title_home)
                showResume()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
//                message.setText(R.string.title_dashboard)
                showProjects()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
//                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_dashboard

    }

    private fun showProjects() {
        if (!hasCurrentFragment() || getCurrentFragment() !is ProjectFragment) {
            replaceFragment(ProjectFragment.newInstance())
        }
    }

    private fun showResume() {
        if (!hasCurrentFragment() || getCurrentFragment() !is ResumeFragment) {
            replaceFragment(ResumeFragment.newInstance())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.commit()
    }

    private fun hasCurrentFragment(): Boolean {
        return fragmentManager != null || fragmentManager.findFragmentById(R.id.content) != null
    }

    private fun getCurrentFragment(): Fragment? {
        return fragmentManager.findFragmentById(R.id.content)
    }

}
