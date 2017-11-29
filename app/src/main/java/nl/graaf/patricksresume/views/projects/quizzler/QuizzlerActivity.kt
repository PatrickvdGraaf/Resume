package nl.graaf.patricksresume.views.projects.quizzler

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.BaseActivity
import nl.graaf.patricksresume.views.projects.quizzler.models.QuestionModel

class QuizzlerActivity : BaseActivity() {
    companion object {
        private val KEY_SCORE = "ScoreKey"
        private val KEY_INDEX = "IndexKey"

        fun getStartIntent(context: Context): Intent {
            return Intent(context, QuizzlerActivity::class.java)
        }
    }


    //TODO Fix Quizzler


    private val mQuestionBank = arrayOf(QuestionModel(R.string.question_1, true),
            QuestionModel(R.string.question_2, true),
            QuestionModel(R.string.question_3, true),
            QuestionModel(R.string.question_4, true),
            QuestionModel(R.string.question_5, true),
            QuestionModel(R.string.question_6, false),
            QuestionModel(R.string.question_7, true),
            QuestionModel(R.string.question_8, false),
            QuestionModel(R.string.question_9, true),
            QuestionModel(R.string.question_10, true),
            QuestionModel(R.string.question_11, false),
            QuestionModel(R.string.question_12, false),
            QuestionModel(R.string.question_13, true))
    private val mProgressBarIncrement: Int = Math.ceil((100 / mQuestionBank.size).toDouble())
            .toInt()

    private val mButtonTrue: Button by bind(R.id.true_button)
    private val mButtonFalse: Button by bind(R.id.true_button)
    private val mTextViewQuestion: TextView by bind(R.id.question_text_view)
    private val mTextViewScore: TextView by bind(R.id.score)
    private val mProgressBar: ProgressBar by bind(R.id.progress_bar)

    private var mIndex: Int = 0
    private var mScore: Int = 0
    @StringRes
    private var mQuestion: Int = mQuestionBank[mIndex].getQuestionStringId()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizzler)

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(KEY_SCORE)
            mIndex = savedInstanceState.getInt(KEY_INDEX)
        }

        mTextViewQuestion.setText(mQuestion)
        mButtonTrue.setOnClickListener({
            checkAnswer(true)
            updateQuestion()
        })
        mButtonFalse.setOnClickListener({
            checkAnswer(false)
            updateQuestion()
        })
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null) {
            outState.putInt(KEY_SCORE, mScore)
            outState.putInt(KEY_INDEX, mIndex)
        }
        super.onSaveInstanceState(outState)
    }

    private fun updateQuestion() {
        mIndex = mIndex++ % mQuestionBank.size

        if (mIndex == 0) {
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.alert_quizzler_game_over))
                    .setCancelable(false)
                    .setMessage(getString(R.string.alert_score_message))
                    .setPositiveButton(getString(R.string.alert_quizzler_close_application),
                            { _: DialogInterface, _: Int ->
                                finish()
                            })
                    .show()
        }

        mQuestion = mQuestionBank[mIndex].getQuestionStringId()
        mTextViewQuestion.setText(mQuestion)
        mProgressBar.incrementProgressBy(mProgressBarIncrement)
        mTextViewScore.text = String.format(getString(R.string.msg_score), mScore,
                mQuestionBank.size)
    }

    private fun checkAnswer(userSelection: Boolean) {
        val correctAnswer = mQuestionBank[mIndex].isAnswer()
        if (userSelection == correctAnswer) {
            mScore++
            Toast.makeText(applicationContext, R.string.correct_toast, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, R.string.incorrect_toast, Toast.LENGTH_SHORT).show()
        }
    }
}
