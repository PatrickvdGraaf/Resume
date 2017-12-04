package nl.graaf.patricksresume.views.projects.githubber.models

/**
 *
 * Created by Patrick van de Graaf on 12/4/2017.
 *
 */
class GithubRepo {
    private var mName = ""
    var owner = ""
    private var mUrl = ""

    fun getName():String{
        return mName
    }

    fun setName(name:String?){
        if (name == null){
            return
        }
        mName = name
    }

    fun setUrl(url:String?){
        if (url == null){
            return
        }
        mUrl = url
    }
}