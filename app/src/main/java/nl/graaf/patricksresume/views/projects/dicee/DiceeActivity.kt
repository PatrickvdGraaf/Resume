package nl.graaf.patricksresume.views.projects.dicee

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dicee.*
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.BaseActivity
import java.util.*

class DiceeActivity : BaseActivity() {

    companion object {
        private val NUM_OF_IMAGES = 6
        fun getStartIntent(context: Context): Intent {
            return Intent(context, DiceeActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dicee)

        val diceArray: IntArray = intArrayOf(
                R.drawable.dice1,
                R.drawable.dice2,
                R.drawable.dice3,
                R.drawable.dice4,
                R.drawable.dice5,
                R.drawable.dice6
        )

        buttonRoll.setOnClickListener({
            try {
                imageViewLeftDice.setImageResource(diceArray[getRandom()])
                imageViewRightDice.setImageResource(diceArray[getRandom()])
            }catch (e:Resources.NotFoundException){

            }

        })
    }

    private fun getRandom() : Int {
        return Random().nextInt(NUM_OF_IMAGES)
    }
}
