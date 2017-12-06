package nl.graaf.patricksresume.views.projects.pixaviewer.views

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
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.projects.pixaviewer.models.Image
import nl.graaf.patricksresume.views.projects.pixaviewer.network.IPixaAPI
import nl.graaf.patricksresume.views.projects.pixaviewer.network.ImageResponse
import nl.graaf.patricksresume.views.projects.pixaviewer.network.deserializer.ImageResponseDeserializer
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.OnItemClickListener
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaAdapter
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaItemClickListener
import okhttp3.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

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

        createPixaAPI()

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
    }

    private fun createPixaAPI() {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(ImageResponse::class.java,
                        ImageResponseDeserializer()).create()

        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build()
        val okHttpClient = OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val originalHttpUrl: HttpUrl = originalRequest.url()
                    val url: HttpUrl = originalHttpUrl.newBuilder()
                            .addQueryParameter("key", IPixaAPI.API_KEY)
                            .build()
                    val requestBuilder: Request.Builder = originalRequest.newBuilder().url(url)
                    chain.proceed(requestBuilder.build())
                }.build()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(IPixaAPI.ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build()

        iPixaAPI = retrofit.create(IPixaAPI::class.java)
        getImages()
    }


    override fun onLongItemClick(view: View, position: Int) {
        //TODO("not implemented")
    }

    override fun onItemClick(view: View, position: Int) {
        val item: Image? = mAdapter.getImageForIndex(position)
        if (item != null) {
            val intent = Intent(this@PixaActivity, PixaDetailActivity::class.java)
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
                        429 -> Snackbar.make(mRecyclerView, "Too many requests in a short time",
                                Snackbar.LENGTH_SHORT)
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
