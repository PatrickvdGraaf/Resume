package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.projects.pixaviewer.models.Image
import nl.graaf.patricksresume.views.projects.pixaviewer.network.IPixaAPI
import nl.graaf.patricksresume.views.projects.pixaviewer.network.ImageResponse
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.OnItemClickListener
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaAdapter
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaItemClickListener
import retrofit2.HttpException
import timber.log.Timber

class PixaSearchActivity : AppCompatActivity(), OnItemClickListener {
    companion object {
        val spanCount = 3
    }

    private var iPixaAPI: IPixaAPI? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mRecyclerView: RecyclerView
    private val mLayoutManager = GridLayoutManager(this@PixaSearchActivity, spanCount)
    private var mAdapter: PixaAdapter = PixaAdapter(this@PixaSearchActivity, ArrayList())
    private var isGettingImages: Boolean = false

    private var mQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixa_search)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (intent.action == Intent.ACTION_SEARCH) {
            mQuery = intent.getStringExtra(SearchManager.QUERY)
        }

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.isNestedScrollingEnabled = true
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = mLayoutManager.childCount
                val totalItemCount = mLayoutManager.itemCount
                val pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition()
                if (pastVisibleItems + visibleItemCount >= totalItemCount - spanCount * 2) {
                    getImages(mQuery)
                }
            }
        })
        mRecyclerView.addOnItemTouchListener(PixaItemClickListener(this@PixaSearchActivity,
                mRecyclerView, this))
        getImages(mQuery)
    }

    override fun onLongItemClick(view: View, position: Int) {
        //TODO("not implemented")
    }

    override fun onItemClick(view: View, position: Int) {
        val item: Image? = mAdapter.getImageForIndex(position)
        if (item != null) {
            val intent = Intent(this@PixaSearchActivity, PixaDetailActivity::class.java)
            intent.putExtra(PixaDetailActivity.BUNDLE_KEY_TITLE,
                    String.format("%sx%s", item.imageWidth, item.imageHeight))
            intent.putExtra(PixaDetailActivity.BUNDLE_KEY_IMAGE, item.webformatURL)
            var options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view,
                    applicationContext.getString(R.string.transition_open_app_name))

            val imageView: ImageView? = view.findViewById(R.id.image)
            if (imageView != null) {
                options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        imageView, applicationContext.getString(R.string.transition_open_app_image))
            }

            startActivity(intent, options.toBundle())
        }
    }

    private fun getImages(query: String) {
        if (!isGettingImages) {
            mAdapter.removeAll()
            compositeDisposable.add(iPixaAPI?.getPictures(getImagePage(), query)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(getImageObserver()))
        }
    }

    private fun getImagePage(): Int {
        return mAdapter.getImagePage()
    }

    private fun getImageObserver(): DisposableSingleObserver<ImageResponse> {
        return object : DisposableSingleObserver<ImageResponse>() {
            override fun onStart() {
                super.onStart()
                isGettingImages = true
            }

            override fun onSuccess(value: ImageResponse) {
                Timber.d(value.toString())
                mAdapter.addObjects(value.getHits())
                isGettingImages = false
            }

            override fun onError(e: Throwable) {
                Timber.e(e, "Failed to get Images")
                if (e is HttpException) {
                    when (e.code()) {
                        429 -> Snackbar.make(mRecyclerView,
                                "Too many requests in a short time",
                                Snackbar.LENGTH_SHORT)
                                .setAction("RETRY", { getImages(mQuery) })
                                .show()
                    }
                }
                isGettingImages = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
}
