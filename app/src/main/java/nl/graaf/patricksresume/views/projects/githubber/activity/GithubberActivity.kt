package nl.graaf.patricksresume.views.projects.githubber.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import nl.graaf.patricksresume.R
import nl.graaf.patricksresume.views.projects.githubber.activity.dialog.CredentialsDialog
import nl.graaf.patricksresume.views.projects.githubber.activity.dialog.ICredentialsDialogListener
import nl.graaf.patricksresume.views.projects.githubber.models.GithubIssue
import nl.graaf.patricksresume.views.projects.githubber.models.GithubRepo
import nl.graaf.patricksresume.views.projects.githubber.network.IGithubAPI
import nl.graaf.patricksresume.views.projects.githubber.network.deseralizer.GithubRepoDeserializer
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GithubberActivity : AppCompatActivity(), ICredentialsDialogListener {
    var githubAPI: IGithubAPI? = null
    var username: String? = null
    var password: String? = null
    var repositoriesSpinner: Spinner? = null
    var issuesSpinner: Spinner? = null
    var commentEditText: EditText? = null
    var sendButton: Button? = null
    var loadReposButtons: Button? = null

    private val compositeDisposable = CompositeDisposable()

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, GithubberActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_githubber)
        val toolbar: android.support.v7.widget.Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)

        sendButton = findViewById(R.id.send_comment_button)

        val repositorySpinner: Spinner = findViewById(R.id.repositories_spinner)
        repositorySpinner.isEnabled = false
        repositoriesSpinner?.adapter = ArrayAdapter(this@GithubberActivity,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("No repositories available"))

        repositoriesSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (parent.selectedItem is GithubRepo) {
                    val githubRepo = parent.selectedItem as GithubRepo
                    compositeDisposable.add(githubAPI?.getIssues(githubRepo.owner,
                            githubRepo.getName())?.subscribeOn(Schedulers.io())?.observeOn(AndroidSchedulers.mainThread())?.subscribeWith(getIssuesObserver()))
                }
            }
        }

        val issuesSpinner: Spinner = findViewById(R.id.issues_spinner)
        issuesSpinner.isEnabled = true
        issuesSpinner.adapter = ArrayAdapter(this@GithubberActivity,
                android.R.layout.simple_spinner_dropdown_item,
                arrayOf("Please select repository"))

        commentEditText = findViewById(R.id.comment_edittext)

        loadReposButtons = findViewById(R.id.loadRepos_button)

        createGitHubAPI()
    }

    override fun onStop() {
        super.onStop()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_githubber, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_credentials -> consume { showCredentialsDialog() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun showCredentialsDialog() {
        val dialog = CredentialsDialog()
        val arguments = Bundle()
        arguments.putString("username", username)
        arguments.putString("password", username)
        dialog.arguments = arguments

        dialog.show(fragmentManager, "")
    }

    private fun createGitHubAPI() {
        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .registerTypeAdapter(GithubRepo::class.java, GithubRepoDeserializer()).create()
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val builder = originalRequest.newBuilder().header("Authorization",
                            Credentials.basic(username, password))
                    val newRequest = builder.build()
                    chain.proceed(newRequest)
                }.build()

        val retrofit: Retrofit = Retrofit.Builder().baseUrl(IGithubAPI.ENDPOINT)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build()

        githubAPI = retrofit.create(IGithubAPI::class.java)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.loadRepos_button -> compositeDisposable.add(githubAPI?.getRepos()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribeWith(getRepositoriesObserver()))
            R.id.send_comment_button -> {
                val newComment = commentEditText?.text.toString()
                if (!newComment.isEmpty()) {
                    val selectedItem = issuesSpinner?.selectedItem as GithubIssue
                    selectedItem.comment = newComment
                    compositeDisposable.add(githubAPI!!.postComment(selectedItem.comments_url,
                            selectedItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(getCommentObserver()))
                } else {
                    Toast.makeText(this@GithubberActivity, "Please enter a comment", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getRepositoriesObserver(): DisposableSingleObserver<List<GithubRepo>> {
        return object : DisposableSingleObserver<List<GithubRepo>>() {
            override fun onSuccess(value: List<GithubRepo>) {
                if (!value.isEmpty()) {
                    val spinnerAdapter = ArrayAdapter(this@GithubberActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            value)
                    repositoriesSpinner?.adapter = spinnerAdapter
                    repositoriesSpinner?.isEnabled = true
                } else {
                    val spinnerAdapter = ArrayAdapter(this@GithubberActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayOf("User has no repositories"))
                    repositoriesSpinner?.adapter = spinnerAdapter
                    repositoriesSpinner?.isEnabled = false
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Toast.makeText(this@GithubberActivity, "Can not load repositories", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIssuesObserver(): DisposableSingleObserver<List<GithubIssue>> {
        return object : DisposableSingleObserver<List<GithubIssue>>() {
            override fun onSuccess(value: List<GithubIssue>) {
                if (!value.isEmpty()) {
                    val spinnerAdapter = ArrayAdapter(this@GithubberActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            value)
                    issuesSpinner?.isEnabled = true
                    commentEditText?.isEnabled = true
                    sendButton?.isEnabled = true
                    issuesSpinner?.adapter = spinnerAdapter
                } else {
                    val spinnerAdapter = ArrayAdapter(this@GithubberActivity,
                            android.R.layout.simple_spinner_dropdown_item,
                            arrayOf("Repository has no issues"))
                    issuesSpinner?.isEnabled = false
                    commentEditText?.isEnabled = false
                    sendButton?.isEnabled = false
                    issuesSpinner?.adapter = spinnerAdapter
                }
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Toast.makeText(this@GithubberActivity, "Can not load issues",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCommentObserver(): DisposableSingleObserver<ResponseBody> {
        return object : DisposableSingleObserver<ResponseBody>() {
            override fun onSuccess(value: ResponseBody) {
                commentEditText?.setText("")
                Toast.makeText(this@GithubberActivity, "Comment created",
                        Toast.LENGTH_LONG).show()
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                Toast.makeText(this@GithubberActivity, "Can not create comment",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDialogPositiveClick(username: String, password: String) {
        this.username = username
        this.password = password
        loadReposButtons?.isEnabled = true
    }

    //The consume function is a very simple one that executes the operation and returns true.
    // I find it very useful for the methods of the Android framework that need to indicate if they
    // have consumed the result.
    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }
}
