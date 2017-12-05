package nl.graaf.patricksresume.views.projects.githubber.network.deseralizer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import nl.graaf.patricksresume.views.projects.githubber.models.GithubRepo
import java.lang.reflect.Type

/**
 *
 * Created by Patrick van de Graaf on 12/4/2017.
 *
 */
class GithubRepoDeserializer : JsonDeserializer<GithubRepo> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?,
                             context: JsonDeserializationContext?): GithubRepo {
        val githubRepo = GithubRepo()

        val repoJsonObject = json?.asJsonObject
        githubRepo.setName(repoJsonObject?.get("name")?.asString!!)
        githubRepo.setUrl(repoJsonObject.get("url")?.asString)

        val ownerJsonElement: JsonElement = repoJsonObject.get("owner")
        val ownerJsonObject: JsonObject = ownerJsonElement.asJsonObject
        githubRepo.setOwner(ownerJsonObject["login"].asString)

        return githubRepo
    }
}