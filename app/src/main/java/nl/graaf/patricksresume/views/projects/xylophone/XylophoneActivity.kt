package nl.graaf.patricksresume.views.projects.xylophone

import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import nl.graaf.patricksresume.R

class XylophoneActivity : AppCompatActivity() {

    companion object {
        // Helpful Constants
        private val NR_OF_SIMULTANEOUS_SOUNDS = 7
        private val LEFT_VOLUME = 1.0f
        private val RIGHT_VOLUME = 1.0f
        private val NO_LOOP = 0
        private val PRIORITY = 0
        private val NORMAL_PLAY_RATE = 1.0f

        fun getStartIntent(context: Context): Intent {
            return Intent(context, XylophoneActivity::class.java)
        }
    }

    private var mSoundPool: SoundPool? = null
    private var mCSoundId: Int = 0
    private var mDSoundId: Int = 0
    private var mESoundId: Int = 0
    private var mFSoundId: Int = 0
    private var mGSoundId: Int = 0
    private var mASoundId: Int = 0
    private var mBSoundId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_xylophone)

        mSoundPool = SoundPool.Builder().setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        ).setMaxStreams(NR_OF_SIMULTANEOUS_SOUNDS).build()

        mCSoundId = mSoundPool!!.load(applicationContext, R.raw.note1_c, PRIORITY)
        mDSoundId = mSoundPool!!.load(applicationContext, R.raw.note2_d, PRIORITY)
        mESoundId = mSoundPool!!.load(applicationContext, R.raw.note3_e, PRIORITY)
        mFSoundId = mSoundPool!!.load(applicationContext, R.raw.note4_f, PRIORITY)
        mGSoundId = mSoundPool!!.load(applicationContext, R.raw.note5_g, PRIORITY)
        mASoundId = mSoundPool!!.load(applicationContext, R.raw.note6_a, PRIORITY)
        mBSoundId = mSoundPool!!.load(applicationContext, R.raw.note7_b, PRIORITY)
    }

    fun playC(view: View) {
        playSound(mCSoundId)
    }

    fun playD(view: View) {
        playSound(mDSoundId)
    }

    fun playE(view: View) {
        playSound(mESoundId)
    }

    fun playF(view: View) {
        playSound(mFSoundId)
    }

    fun playG(view: View) {
        playSound(mGSoundId)
    }

    fun playA(view: View) {
        playSound(mASoundId)
    }

    fun playB(view: View) {
        playSound(mBSoundId)
    }

    private fun playSound(sound: Int) {
        mSoundPool!!.play(sound, LEFT_VOLUME, RIGHT_VOLUME, PRIORITY, NO_LOOP, NORMAL_PLAY_RATE)
    }
}
