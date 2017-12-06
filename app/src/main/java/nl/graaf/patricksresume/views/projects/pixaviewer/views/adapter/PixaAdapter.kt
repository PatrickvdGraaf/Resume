package nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.GlideApp
import nl.graaf.patricksresume.views.projects.pixaviewer.models.Image
import timber.log.Timber


/**
 * Created by patrick on 12/5/17.
 * 1:59 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class PixaAdapter(context: Context, objects: ArrayList<Image>)
    : RecyclerView.Adapter<PixaAdapter.PixaViewHolder>() {

    private val mData = objects
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PixaViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.grid_item_pixa_image, parent, false)
        return PixaViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: PixaViewHolder?, position: Int) {
        holder?.setImage(mContext, mData[position])
    }

    fun getImageForIndex(index: Int): Image? {
        if (index <= itemCount) {
            return mData[index]
        }
        return null
    }

    fun add(position: Int, item: Image) {
        mData.add(item)
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun addObjects(objects: ArrayList<Image>) {
        for (image in objects) {
            add(itemCount, image)
        }
    }

    fun getImagePage(): Int {
        val page: Int = mData.size / 20
        if (page <= 0) {
            return 1
        }
        return page + 1
    }

    inner class PixaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mTextView: TextView = itemView.findViewById(R.id.text)
        private var mImageView: ImageView = itemView.findViewById(R.id.image)
        private var mBackground: ConstraintLayout = itemView.findViewById(R.id.background)

        fun setImage(context: Context, image: Image) {
            if (image.getRGB() != 0) {
                mBackground.setBackgroundColor(image.getRGB())
            }
            if (image.getPrimaryTextColor() != 0) {
                mTextView.setTextColor(image.getPrimaryTextColor())
            }
            GlideApp.with(context)
                    .load(image.webformatURL)
                    .centerCrop()
                    .thumbnail(GlideApp.with(context)
                            .load("https://www.telesurtv.net/arte/loading2.gif"))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?,
                                                  model: Any?,
                                                  target: Target<Drawable>?,
                                                  isFirstResource: Boolean): Boolean {
                            Timber.e(e)
                            return true
                        }

                        override fun onResourceReady(resource: Drawable?,
                                                     model: Any?,
                                                     target: Target<Drawable>?,
                                                     dataSource: DataSource?,
                                                     isFirstResource: Boolean): Boolean {
                            if (resource != null) {
                                mImageView.setImageDrawable(resource)
                                Palette.from(drawableToBitmap(resource))
                                        .generate { palette ->
                                            val textSwatch: Palette.Swatch? = palette.vibrantSwatch
                                            if (textSwatch != null) {
                                                mTextView.setTextColor(textSwatch.titleTextColor)
                                                setBackgroundColor(textSwatch.rgb)
                                                image.setRGB(textSwatch.rgb)
                                                image.setPrimaryTextColor(textSwatch.titleTextColor)
                                            }
                                        }
                            } else {
                                TODO("ImageView should be set to a placeholder")
                            }
                            return true
                        }

                    }).into(mImageView)
            mTextView.text = String.format("%sx%s", image.imageWidth, image.imageHeight)
        }

        private fun setBackgroundColor(color: Int) {
            mTextView.setBackgroundColor(color)
//            mTextView.alpha = 0.7f
        }

        private fun drawableToBitmap(drawable: Drawable): Bitmap {
            val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0
                    || drawable.intrinsicHeight <= 0) {
                // Single color bitmap will be created of 1x1 pixel
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight,
                        Bitmap.Config.ARGB_8888)
            }
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap!!
        }
    }
}