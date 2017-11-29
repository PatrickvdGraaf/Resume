package nl.graaf.patricksresume.views.projects.dicee

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.DrawableRes
import kotlinx.android.synthetic.main.activity_dicee.*
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.BaseActivity
import timber.log.Timber
import java.util.*

class DiceeActivity : BaseActivity() {

    companion object {
        private val NUM_OF_IMAGES = 6

        private val KEY_DICE_LEFT = "KEY_DICE_LEFT"
        private val KEY_DICE_RIGHT = "KEY_DICE_RIGHT"
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DiceeActivity::class.java)
        }
    }

    private val diceArray: IntArray = intArrayOf(
            R.drawable.dice1,
            R.drawable.dice2,
            R.drawable.dice3,
            R.drawable.dice4,
            R.drawable.dice5,
            R.drawable.dice6
    )

    @DrawableRes
    private var mImageIdLeft = R.drawable.dice1
    @DrawableRes
    private var mImageIdRight = R.drawable.dice2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dicee)

        if (savedInstanceState != null) {
            updateLeftDice(savedInstanceState.getInt(KEY_DICE_LEFT, R.drawable.dice1))
            updateRightDice(savedInstanceState.getInt(KEY_DICE_RIGHT, R.drawable.dice2))
        }

        buttonRoll.setOnClickListener({
            try {
                updateLeftDice(diceArray[getRandom()])
                updateRightDice(diceArray[getRandom()])
            } catch (e: Resources.NotFoundException) {
                Timber.e(e, "Dicee could not find a matching resource")
            }

        })
    }

    private fun updateLeftDice(imageRes: Int) {
        try {
            mImageIdLeft = imageRes
            imageViewLeftDice.setImageResource(mImageIdLeft)
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "Dicee could not find a matching resource")
        }
    }

    private fun updateRightDice(imageRes: Int) {
        try {
            mImageIdRight = imageRes
            imageViewRightDice.setImageResource(mImageIdRight)
        } catch (e: Resources.NotFoundException) {
            Timber.e(e, "Dicee could not find a matching resource")
        }

    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        if (outState != null) {
            outState.putInt(KEY_DICE_LEFT, mImageIdLeft)
            outState.putInt(KEY_DICE_RIGHT, mImageIdRight)
        }
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun getRandom(): Int {
        return Random().nextInt(NUM_OF_IMAGES)
    }
}
