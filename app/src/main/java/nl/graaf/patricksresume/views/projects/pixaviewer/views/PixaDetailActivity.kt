package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.GlideApp

class PixaDetailActivity : AppCompatActivity() {

    companion object {
        val BUNDLE_KEY_TITLE: String = "BUNDLE_KEY_TITLE"
        val BUNDLE_KEY_IMAGE: String = "BUNDLE_KEY_IMAGE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixa_detail)

        val title = intent.getStringExtra(BUNDLE_KEY_TITLE)
        val imageUrl = intent.getStringExtra(BUNDLE_KEY_IMAGE)
        val titleTextView: TextView = findViewById(R.id.title)
        titleTextView.text = title
        val imageView: ImageView = findViewById(R.id.image)
        GlideApp.with(this)
                .load(imageUrl)
                .thumbnail(Glide.with(this).load("https://goo.gl/f39bBS"))
                .centerInside()
                .into(imageView)
    }
}
