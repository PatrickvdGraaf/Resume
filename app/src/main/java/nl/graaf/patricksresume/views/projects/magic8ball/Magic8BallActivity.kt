package nl.graaf.patricksresume.views.projects.magic8ball

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.annotation.Size
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import nl.graaf.patricksresume.R
import timber.log.Timber
import java.util.*

/**
 * This Activity shows the basic understanding of creating an Activity, implementing an Interface
 * and updating an Image when the user preforms certain actions.
 */
class Magic8BallActivity : AppCompatActivity(), SensorEventListener {
    private val ballsArray = intArrayOf(R.drawable.ball1, R.drawable.ball2, R.drawable.ball3, R.drawable.ball4, R.drawable.ball5)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val buttonAsk: Button by bind(R.id.buttonAsk)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val imageViewBall: ImageView by bind(R.id.imageViewBall)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    var listener: OnShakeListener? = null
    private var mSensorManager: SensorManager? = null
    private var mShakeTimestamp: Long = 0
    private var mAccelerometer: Sensor? = null

    companion object {
        /*
     * The gForce that is necessary to register as shake.
	 * Must be greater than 1G (one earth gravity unit).
	 */
        private val SHAKE_THRESHOLD_GRAVITY = 1.3f
        private val SHAKE_SLOP_TIME_MS = 500

        private val ANIM_PREPARE_DURATION = 70
        private val ANIM_SHAKE_DURATION = 140
        private val ANIM_RESET_DURATION = 120
        fun getStartIntent(context: Context): Intent {
            return Intent(context, Magic8BallActivity::class.java)
        }
    }

    @VisibleForTesting
    val animationDuration: Int
        get() = ANIM_PREPARE_DURATION + ANIM_SHAKE_DURATION + ANIM_RESET_DURATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eight_ball)

        buttonAsk.setOnClickListener { updateBallView() }

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // ShakeDetector initialization
        if (mSensorManager != null) {
            mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            setOnShakeListener(object : OnShakeListener {
                override fun onShake() {
                    updateBallView()
                }
            })
        } else {
            Timber.i("Could not find System Service for SENSOR_SERVICE, the device might not support sensors.")
        }
    }

    /**
     * Update the imageViewBall with a randomly chosen image.
     */
    fun updateBallView() {
        setBallImage(getFromBallsArray(Random().nextInt(ballsArray.size)))
    }

    /**
     * Used for testing in Magic8BallTest. By defining the random number in the Test, we can
     * check if the right image from the ballsArray is set into the imageViewBall.
     *
     *
     * Since the Test can't call imageViewBall.getImageResource, we put the id of the Drawable into
     * the imageViewBall Tag.
     *
     * @param randomNum A randomly generated number from the test method. Used to set the Ball Image
     * from outside the Magic8BallActivity.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateBallView(@Size(min = 0, max = 5) randomNum: Int) {
        setBallImage(ballsArray[randomNum])
    }

    fun getBallsArrayLength(): Int {
        return ballsArray.size
    }

    @DrawableRes
    fun getFromBallsArray(index: Int): Int {
        if (index < 0) {
            return ballsArray[0]
        }
        return ballsArray[index]
    }

    /**
     * This method handles the animations while changing the image.
     * Since the shaking will happen between -10% and -10%, I first start a prepare animation that
     * moves the view to -10% and pun it there with setFillAfter(true)
     *
     *
     * Using AnimationListeners, the second, 'real' shaking animation starts right after the first
     * one ends.
     *
     * @param image Image Drawable from the ballsArray
     */
    private fun setBallImage(@DrawableRes image: Int) {
        val prepareAnimation = AnimationUtils.loadAnimation(applicationContext,
                R.anim.prepare)
        prepareAnimation.fillAfter = true
        prepareAnimation.duration = ANIM_PREPARE_DURATION.toLong()
        val shakeAnimation = AnimationUtils.loadAnimation(applicationContext,
                R.anim.shake)
        shakeAnimation.fillAfter = true
        shakeAnimation.duration = ANIM_SHAKE_DURATION.toLong()
        shakeAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                imageViewBall.setImageResource(image)
                imageViewBall.tag = image

                val resetAnimation = AnimationUtils.loadAnimation(applicationContext,
                        R.anim.reset)
                resetAnimation.fillAfter = true
                resetAnimation.duration = ANIM_RESET_DURATION.toLong()
                imageViewBall.startAnimation(resetAnimation)
            }

            override fun onAnimationStart(animation: Animation) {
                //ignore
            }

            override fun onAnimationRepeat(animation: Animation) {
                //ignore
            }
        })
        prepareAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                imageViewBall.startAnimation(shakeAnimation)
            }

            override fun onAnimationStart(animation: Animation) {
                //ignore
            }

            override fun onAnimationRepeat(animation: Animation) {
                //ignore
            }
        })
        imageViewBall.startAnimation(prepareAnimation)
    }

    /**
     * If a listener exists (if the device has sensor services) we use the coordinates in the
     * sensorEvent to determine whether a shake has occurred and if it was severe enough to
     * trigger the updateBallView method.
     *
     * @param event SensorEvent indicating if and how much a device has moved/tilted/turned.
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (listener != null) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // gForce will be close to 1 when there is no movement.
            val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble())

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                val now = System.currentTimeMillis()
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return
                }
                mShakeTimestamp = now
                listener!!.onShake()
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI)
    }

    public override fun onPause() {
        mSensorManager!!.unregisterListener(this)
        super.onPause()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // ignore
    }

    private fun setOnShakeListener(listener: OnShakeListener) {
        this.listener = listener
    }

    interface OnShakeListener {
        fun onShake()
    }

    private fun <T : View> Magic8BallActivity.bind(@IdRes res: Int): Lazy<T> =
            kotlin.lazy(kotlin.LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
}

