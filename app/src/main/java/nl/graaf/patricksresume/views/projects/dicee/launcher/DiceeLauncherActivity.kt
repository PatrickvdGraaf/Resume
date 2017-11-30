package nl.graaf.patricksresume.views.projects.dicee.launcher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.BaseActivity
import nl.graaf.patricksresume.views.projects.dicee.DiceeActivity

class DiceeLauncherActivity : BaseActivity() {
    companion object {
        private val TRANSITION_DELAY_MILLIS: Long = 1500
        private val FINISH_DELAY_MILLIS: Long = 100
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DiceeLauncherActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dicee_launcher)
        val handler = Handler()

        handler.postDelayed({
            startActivity(DiceeActivity.getStartIntent(applicationContext))
            handler.postDelayed({finish()}, FINISH_DELAY_MILLIS)
        }, TRANSITION_DELAY_MILLIS)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
