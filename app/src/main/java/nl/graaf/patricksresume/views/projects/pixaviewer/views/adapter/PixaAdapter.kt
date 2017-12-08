package nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.helpers.GlideApp
import nl.graaf.patricksresume.views.projects.pixaviewer.models.PixaImage
import timber.log.Timber


/**
 * Created by patrick on 12/5/17.
 * 1:59 PM, Notive B.V.
 *
 * © Copyright 2017
 */
class PixaAdapter(activity: Activity, objects: ArrayList<PixaImage>)
    : RecyclerView.Adapter<PixaAdapter.PixaViewHolder>() {

    private var mData: ArrayList<PixaImage> = objects
    private var mScreenWidth: Int = getScreenWidth(activity)
    private val mContext: Context = activity.applicationContext

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PixaViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.grid_item_pixa_image, parent, false)
        return PixaViewHolder(view)
    }

    private fun getScreenWidth(activity: Activity): Int {
        val wm: WindowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val size = Point()
        wm.defaultDisplay.getSize(size)
        return size.x
    }

    fun getItems(): ArrayList<PixaImage> {
        return mData
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: PixaViewHolder?, position: Int) {
        holder?.setImage(mContext, mData[position], position)
    }

    fun getImageForIndex(index: Int): PixaImage? {
        if (index <= itemCount) {
            return mData[index]
        }
        return null
    }

    fun add(position: Int, item: PixaImage) {
        mData.add(item)
        notifyItemInserted(position)
    }

    fun removeAll() {
        while (itemCount != 0) {
            remove(itemCount - 1)
        }
    }

    private fun remove(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setData(images: ArrayList<PixaImage>) {
        mData = images
        notifyDataSetChanged()
    }

    fun addObjects(objects: ArrayList<PixaImage>) {
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
//        private var mTextView: TextView = itemView.findViewById(R.id.text)
//        private var mContent: View = itemView.findViewById(R.id.content)
        private var mImageView: ImageView = itemView.findViewById(R.id.image)
//        private var mProgressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        fun setImage(context: Context, image: PixaImage, position: Int) {
            val opts = BitmapFactory.Options()
            opts.inJustDecodeBounds = true
            BitmapFactory.decodeFile(image.webformatURL, opts)
            opts.inJustDecodeBounds = false

            val height: Int = if (position == 1 || position == (mData.size - 1)) {
                150
            } else {
                300
            }

            var listener: RequestListener<Drawable?>? = null
            if (!image.hasColors()) {
                listener = object : RequestListener<Drawable?> {
                    override fun onResourceReady(resource: Drawable?,
                                                 model: Any?, target: Target<Drawable?>?,
                                                 dataSource: DataSource?,
                                                 isFirstResource: Boolean): Boolean {
                        if (resource != null) {
//                            mProgressBar.visibility = View.GONE
//                            mContent.visibility = View.VISIBLE
                            Palette.from(drawableToBitmap(resource))
                                    .generate { palette ->
                                        image.setPaletteValues(palette)
                                        updateColors(image)
                                    }
                        }
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?,
                                              model: Any?,
                                              target: Target<Drawable?>?,
                                              isFirstResource: Boolean): Boolean {
                        Timber.e(e)
//                        mProgressBar.visibility = View.GONE
//                        mContent.visibility = View.GONE
                        //TODO show sad face :(
                        return true
                    }
                }
            } else {
                updateColors(image)
            }

            GlideApp.with(context)
                    .load(image.webformatURL)
                    .override(mScreenWidth / 2, height)
                    .centerCrop()
                    .listener(listener)
                    .into(mImageView)

//            mTextView.text = String.format("%sx%s", image.imageWidth, image.imageHeight)
        }

        private fun updateColors(pixaImage: PixaImage) {
//            mContent.setBackgroundColor(pixaImage.rgb)
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