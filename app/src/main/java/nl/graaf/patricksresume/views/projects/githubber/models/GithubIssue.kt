package nl.graaf.patricksresume.views.projects.githubber.models

import com.google.gson.annotations.SerializedName



/**
 *
 * Created by Patrick van de Graaf on 12/4/2017.
 *
 */
class GithubIssue {
    var id: String = ""
    var title: String = ""
    var comments_url: String = ""

    @SerializedName("body")
    var comment: String = ""
}

