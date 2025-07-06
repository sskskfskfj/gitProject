package kotlin1.kopring.controller

import kotlin1.kopring.Dto.CustomOauth2User
import kotlin1.kopring.service.RepoService
import mu.KotlinLogging
import net.minidev.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import java.security.Principal

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/repo")
class RepoController(
    private val webClient: WebClient,
    private val repoService: RepoService,
) {
    @GetMapping("/get")
    fun getRepos(
        @AuthenticationPrincipal oAuth2User: OAuth2User?,
        @RequestParam(value = "number", required = false) number: Int,
    ) : ResponseEntity<Any>{
        oAuth2User ?: return ResponseEntity.status(401).body("로그인을 먼저 진행해주세요")

        val accessToken = getUserTokenFromPrincipal(oAuth2User)["token"] as String

        val repos = webClient.get()
            .uri("user/repos?page=$number&per_page=5")
            .header("authorization", "Bearer $accessToken")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        val result = repoService.parseJSON(repos ?: "not found")
        logger.info {result}
        return ResponseEntity.ok().body(result)
    }

    @GetMapping("/page")
    fun getSinglePages(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestParam(value = "repo", required = true) repoName : String,
    ) : ResponseEntity<Any>{
        val accessToken = getUserTokenFromPrincipal(oAuth2User)["token"] as String
        val username = getUserTokenFromPrincipal(oAuth2User)["username"] as String

        return ResponseEntity.ok().body(repoService.getAllPagesInRepo(username, accessToken, repoName))
    }

    @GetMapping("/{repo}/{file}")
    fun getSingleFile(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @PathVariable("file") fileName: String,
        @PathVariable("repo") repoName: String,
    ) : ResponseEntity<Any>{
        val accessToken = getUserTokenFromPrincipal(oAuth2User)["token"] as String
        val username = getUserTokenFromPrincipal(oAuth2User)["username"] as String

        return ResponseEntity.ok().body(repoService.getContent(
            repoName, fileName, username, accessToken
        ))
    }

    fun getUserTokenFromPrincipal(oAuth2User: OAuth2User) : Map<String, String> {
        val user = oAuth2User as CustomOauth2User
        val token = user.getToken()
        val username = user.name
        return mapOf(
            "username" to username,
            "token" to token
        )
    }
}