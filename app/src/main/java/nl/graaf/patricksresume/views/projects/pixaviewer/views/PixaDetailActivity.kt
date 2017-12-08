package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.GlideApp
import nl.graaf.patricksresume.views.projects.pixaviewer.models.PixaImage
import nl.graaf.patricksresume.views.projects.pixaviewer.views.color.ColorUtils
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
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

        mPixaImage = intent.getParcelableExtra(BUNDLE_KEY_IMAGE)

        val titleTextView: TextView = findViewById(R.id.title)
        val bodyTextView: TextView = findViewById(R.id.tags)
        val imageView: ImageView = findViewById(R.id.image)
        imageView.setOnClickListener({
            toggleHideyBar()
        })

        supportPostponeEnterTransition()

        GlideApp.with(this@PixaDetailActivity)
                .load(mPixaImage.webformatURL)
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?,
                                                 target: Target<Drawable>?, dataSource: DataSource?,
                                                 isFirstResource: Boolean): Boolean {
                        this@PixaDetailActivity.supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?,
                                              target: Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {
                        Timber.e(e)
                        return true
                    }
                })
                .into(imageView)

        val contentView = findViewById<View>(R.id.content)
        contentView.setBackgroundColor(ColorUtils.modifyAlpha(mPixaImage.rgb,
                PixaImage.BACKGROUND_ALPHA_FLOAT))
        contentView.setPadding(contentView.paddingStart,
                contentView.paddingTop,
                contentView.paddingEnd,
                contentView.paddingBottom
                        + (supportActionBar?.height ?: 52))

        titleTextView.text = String.format("%sx%s", mPixaImage.imageWidth, mPixaImage.imageHeight)
        bodyTextView.text = mPixaImage.tags.toString()
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
        if (isImmersiveModeEnabled) {
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
        actionBar?.hide()
    }

    private fun toggleContentView(isImmersiveModeEnabled: Boolean) {
        val contentView: View = findViewById<View>(R.id.content)
        contentView.animate()
                .translationY(if (isImmersiveModeEnabled) {
                    1.0f
                } else {
                    val resources = resources
                    val resourceId = resources.getIdentifier("navigation_bar_height",
                            "dimen",
                            "android")
                    contentView.height.toFloat() + resources.getDimensionPixelSize(resourceId)
                })
                .setListener(null)
    }
}