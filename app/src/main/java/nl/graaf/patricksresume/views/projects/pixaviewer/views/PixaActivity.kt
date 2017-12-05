package nl.graaf.patricksresume.views.projects.pixaviewer.views

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
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
import nl.graaf.patricksresume.views.projects.pixaviewer.views.adapter.PixaAdapter
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.*

class PixaActivity : AppCompatActivity(), AdapterView.OnItemClickListener {
    private var iPixaAPI: IPixaAPI? = null
    private val compositeDisposable = CompositeDisposable()
    private lateinit var mGridView: GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pixa)

        mGridView = findViewById(R.id.gridView)
        mGridView.onItemClickListener = this

        createPixaAPI()
    }

    override fun onItemClick(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position) as Image
        val intent = Intent(this@PixaActivity, PixaDetailActivity::class.java)
        intent.putExtra(PixaDetailActivity.BUNDLE_KEY_TITLE, String.format("%sx%s", item.imageWidth,
                item.imageHeight))
        intent.putExtra(PixaDetailActivity.BUNDLE_KEY_IMAGE, item.webformatURL)
        startActivity(intent)
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
        compositeDisposable.add(iPixaAPI?.getPictures()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribeWith(getImageObserver()))
    }

    private fun getImageObserver(): DisposableSingleObserver<ImageResponse> {
        return object : DisposableSingleObserver<ImageResponse>() {
            override fun onSuccess(value: ImageResponse) {
                Timber.d(value.toString())
                mGridView.adapter = PixaAdapter(this@PixaActivity,
                        R.layout.grid_item_pixa_image,
                        value.getHints())
            }

            override fun onError(e: Throwable) {
                Timber.e(e, "Failed to get Github Repo's")
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
