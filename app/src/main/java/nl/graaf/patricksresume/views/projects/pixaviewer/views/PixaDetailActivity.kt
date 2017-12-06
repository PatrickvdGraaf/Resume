package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.readystatesoftware.systembartint.SystemBarTintManager
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.GlideApp
import nl.graaf.patricksresume.views.projects.pixaviewer.models.PixaImage
import timber.log.Timber

class PixaDetailActivity : AppCompatActivity() {

    companion object {
        val BUNDLE_KEY_IMAGE: String = "BUNDLE_KEY_IMAGE"

        fun getStartIntent(context: Context, pixaImage: PixaImage): Intent {
            val intent = Intent(context, PixaDetailActivity::class.java)
            intent.putExtra(BUNDLE_KEY_IMAGE, pixaImage)
            return intent
        }
    }

    private lateinit var mPixaImage: PixaImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixa_detail)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        val tintManager = SystemBarTintManager(this@PixaDetailActivity)
        tintManager.isStatusBarTintEnabled = true
        tintManager.setNavigationBarTintEnabled(true)

        mPixaImage = intent.getParcelableExtra(BUNDLE_KEY_IMAGE)

        val titleTextView: TextView = findViewById(R.id.title)
        val imageView: ImageView = findViewById(R.id.image)
        imageView.setOnClickListener({
            toggleHideyBar()
        })

        //TODO use bitmap from memory to bypass reloading
        GlideApp.with(this@PixaDetailActivity)
                .load(mPixaImage.webformatURL)
                .centerInside()
                .into(imageView)

        val contentView = findViewById<View>(R.id.content)
        contentView.setBackgroundColor(mPixaImage.getRGB())
        contentView.alpha = PixaImage.BACKGROUND_ALPHA

        titleTextView.text = String.format("%sx%s", mPixaImage.imageWidth, mPixaImage.imageHeight)
//        titleTextView.setTextColor(mPixaImage.getPrimaryTextColor())

//        tintManager.setStatusBarAlpha(PixaImage.BACKGROUND_ALPHA)
        Timber.d("%s", mPixaImage.getRGB())
        window.statusBarColor = mPixaImage.getRGB()
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    private fun toggleHideyBar() {
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = ((uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                == uiOptions)
        if (isImmersiveModeEnabled){
            Timber.d("Turning immersive mode mode off. ")
        } else {
            Timber.d("Turning immersive mode mode on.")
        }
        toggleContentView(isImmersiveModeEnabled)

        // Navigation bar hiding: Backwards compatible to ICS.
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN

        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN. For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = newUiOptions
    }

    private fun toggleContentView(isImmersiveModeEnabled: Boolean){
        val contentView: View = findViewById<View>(R.id.content)
        contentView.animate()
                .translationY(if (isImmersiveModeEnabled){
                    1.0f
                } else {
                    contentView.height.toFloat()
                })
                .setListener(null)
    }
}