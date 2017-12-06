package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.projects.pixaviewer.models.PixaImage
import nl.graaf.patricksresume.views.projects.pixaviewer.network.ClientBuilder
import nl.graaf.patricksresume.views.projects.pixaviewer.network.IPixaAPI
import nl.graaf.patricksresume.views.projects.pixaviewer.network.ImageResponse
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.OnItemClickListener
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaAdapter
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaItemClickListener
import retrofit2.HttpException
import timber.log.Timber

class PixaActivity : AppCompatActivity(), OnItemClickListener {
    companion object {
        val spanCount = 3
    }

    private var iPixaAPI: IPixaAPI? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mRecyclerView: RecyclerView
    private val mLayoutManager = GridLayoutManager(this@PixaActivity, spanCount)
    private var mAdapter: PixaAdapter = PixaAdapter(this@PixaActivity, ArrayList())
    private var isGettingImages: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixa)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = "PixaViewer"

        iPixaAPI = ClientBuilder.getPixaAPIClient()

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
                    getImages()
                }
            }
        })
        mRecyclerView.addOnItemTouchListener(PixaItemClickListener(this@PixaActivity,
                mRecyclerView, this))

        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL)

        getImages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_pixa, menu)
        // Associate searchable configuration with the SearchView
//        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//        val searchView: SearchView = menu?.findItem(R.id.search)?.actionView as SearchView
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.search -> onSearchRequested()
        else -> super.onOptionsItemSelected(item)
    }

    override fun onLongItemClick(view: View, position: Int) {
        //TODO("not implemented")
    }

    override fun onItemClick(view: View, position: Int) {
        val pixaImage: PixaImage? = mAdapter.getImageForIndex(position)
        if (pixaImage != null) {
            val intent = PixaDetailActivity.getStartIntent(this@PixaActivity, pixaImage)
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

    private fun getImages() {
        if (!isGettingImages) {
            compositeDisposable.add(iPixaAPI?.getPictures(getImagePage())
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
                                "Too many requests in a short time", Snackbar.LENGTH_SHORT)
                                .setAction("RETRY", { getImages() })
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
