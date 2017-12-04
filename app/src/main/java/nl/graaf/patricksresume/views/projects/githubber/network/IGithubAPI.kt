package nl.graaf.patricksresume.views.projects.githubber.network

import io.reactivex.Single
import nl.graaf.patricksresume.views.projects.githubber.models.GithubIssue
import nl.graaf.patricksresume.views.projects.githubber.models.GithubRepo
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 *
 * Created by Patrick van de Graaf on 12/4/2017.
 *
 */
interface IGithubAPI {
    companion object {
        val ENDPOINT = "https://api.github.com"
    }

    @GET("user/repos?per_page=100")
    fun getRepos(): Single<List<GithubRepo>>

    @GET("/repos/{owner}/{repo}/issues")
    fun getIssues(@Path("owner") owner: String,
                  @Path("repo") repository: String): Single<List<GithubIssue>>

    @POST()
    fun postComment(@Url url: String, @Body issue: GithubIssue): Single<ResponseBody>
}