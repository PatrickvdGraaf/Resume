package nl.graaf.patricksresume.views.projects.destini

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import nl.graaf.patricksresume.R

class DestiniActivity : AppCompatActivity() {
    private val mTextViewStory: TextView by bind(R.id.storyTextView)
    private val mButtonTop: Button by bind(R.id.buttonTop)
    private val mButtonBottom: Button by bind(R.id.buttonBottom)

    private var mStoryIndex: Int = 1

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DestiniActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTextViewStory.setText(R.string.T1_Story)
        mButtonTop.setText(R.string.T1_Ans1)
        mButtonBottom.setText(R.string.T1_Ans2)

        mButtonTop.setOnClickListener({
            when (mStoryIndex) {
                1, 2 -> {
                    mTextViewStory.setText(R.string.T3_Story)
                    mButtonTop.setText(R.string.T3_Ans1)
                    mButtonBottom.setText(R.string.T3_Ans2)
                    mStoryIndex = 3
                }
                3 -> {
                    mTextViewStory.setText(R.string.T6_End)
                    onEndReached()
                }
            }
        })

        mButtonBottom.setOnClickListener({
            when (mStoryIndex) {
                1 -> {
                    mTextViewStory.setText(R.string.T2_Story)
                    mButtonTop.setText(R.string.T2_Ans1)
                    mButtonBottom.setText(R.string.T2_Ans2)
                    mStoryIndex = 2
                }
                2 -> {
                    mTextViewStory.setText(R.string.T4_End)
                    onEndReached()
                }
                3 -> {
                    mTextViewStory.setText(R.string.T5_End)
                    onEndReached()
                }
            }
        })
    }

    private fun onEndReached() {
        mButtonTop.visibility = View.GONE
        mButtonBottom.visibility = View.GONE
    }

    private fun <T : View> DestiniActivity.bind(@IdRes res: Int): Lazy<T> =
            kotlin.lazy(kotlin.LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
}
