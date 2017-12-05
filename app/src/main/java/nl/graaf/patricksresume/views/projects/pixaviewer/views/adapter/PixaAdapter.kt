package nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v7.graphics.Palette
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
class PixaAdapter(context: Context, textViewResourceId: Int, objects: ArrayList<Image>)
    : ArrayAdapter<Image>(context, textViewResourceId, objects) {
    private val mContext: Context = context
    private val mLayoutResourceId: Int = textViewResourceId
    private val mData = objects

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var row: View? = convertView
        val holder: PixaViewHolder?

        if (row == null) {
            val inflater: LayoutInflater = (context as Activity).layoutInflater
            row = inflater.inflate(mLayoutResourceId, parent, false)
            holder = PixaViewHolder()
            holder.setView(row)
            row.tag = holder
        } else {
            holder = row.tag as PixaViewHolder?
        }

        if (row == null) {
            Timber.e("Could not inflate PixaImage-view for GridView")
            return super.getView(position, convertView, parent)
        }

        val item: Image = mData[position]
        holder?.setImage(mContext, item)

        return row
    }

    inner class PixaViewHolder {
        private lateinit var mTextView: TextView
        private lateinit var mImageView: ImageView
        private lateinit var mBackground: ConstraintLayout

        fun setView(view: View) {
            mTextView = view.findViewById(R.id.text)
            mImageView = view.findViewById(R.id.image)
            mBackground = view.findViewById(R.id.background)
        }

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
                    .thumbnail(0.1f)
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
            mBackground.setBackgroundColor(color)
            mBackground.alpha = 0.7f
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