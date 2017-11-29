package nl.graaf.patricksresume.views.helpers

import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View

open class BaseActivity : AppCompatActivity() {
    fun <T : View> BaseActivity.bind(@IdRes res: Int): Lazy<T> =
            kotlin.lazy(kotlin.LazyThreadSafetyMode.NONE) { findViewById<T>(res) }
}
