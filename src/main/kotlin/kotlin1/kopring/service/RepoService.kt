package kotlin1.kopring.service

import kotlin1.kopring.Dto.ResponseDto
import mu.KotlinLogging
import org.json.JSONArray
import org.json.JSONObject
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.util.*


private val logger = KotlinLogging.logger {}

@Service
class RepoService (
    private val webClient: WebClient,

){
    private var files = mutableListOf<Any>()
    private val baseUrl = "https://api.github.com/"

    fun parseJSON(json : String) : ResponseDto<MutableList<Map<String, String>>> {
        val jsonArray = JSONArray(json)
        val infoList = mutableListOf<Map<String, String>>()

        for(i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val map = mutableMapOf<String, String>()

            map["name"] = jsonObject.getString("name")
//            map["update"] = jsonObject.getString("updated_at").toString()
            infoList.add(map)
        }

        return ResponseDto(
            status = 200,
            message = "repo",
            data = infoList
        )
    }

    fun getAllPagesInRepo(username : String, token : String, repoName : String) : ResponseDto<MutableList<Any>>{
        val repos = githubCall("$baseUrl/repos/${username}/${repoName}/contents", token) as MutableList<*>
        files.clear()

        getAllFiles(repos, username, repoName, token)
        logger.info { files }

        return ResponseDto(
            status = 200,
            message = "all pages in repo",
            data = files
        )
    }

    fun githubCall(url : String, token : String) : MutableList<*>?{
        return webClient.get()
            .uri(url)
            .header("authorization", "Bearer $token")
            .header("Content-Type", "application/json")
            .header("Accept", "application/vnd.github+json")
            .retrieve()
            .bodyToMono(MutableList::class.java)
            .block()
    }

    fun getAllFiles(
        list : MutableList<*>,
        username : String,
        repoName: String,
        token : String,
    ){

        val url = "$baseUrl/repos/$username/$repoName/contents/"
        for(element in list){
            element as Map<*, *>

            if(element["type"]!! == "dir"){
                val dirName = element["path"] as String
                val repos = githubCall("$url$dirName", token) as MutableList<*>

                getAllFiles(repos, username, repoName, token)
            }else{
                files.add(element["name"]!!)
            }
        }
    }

    fun getContent(
        repoName : String,
        fileName: String,
        username : String,
        token : String,
    ) : ResponseDto<String> {
        val url = "$baseUrl/repos/$username/$repoName/contents/$fileName"
        val result  = webClient.get()
            .uri(url)
            .header("authorization", "Bearer $token")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val jsonObject = JSONObject(result)
        val byte : String = jsonObject.get("content").toString()
        val code = decodeBASE64(byte)
        logger.info { code }

        return ResponseDto(
            status = 200,
            message = "content",
            data = code
        )
    }

    fun decodeBASE64(base64: String): String {
        val cleaned = base64.replace("\n", "")
        val decodedBytes = Base64.getDecoder().decode(cleaned)
        return String(decodedBytes, Charsets.UTF_8)
    }
}