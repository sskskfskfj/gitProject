package kotlin1.kopring.controller

import kotlin1.kopring.Dto.CustomOauth2User
import kotlin1.kopring.service.RepoService
import mu.KotlinLogging
import net.minidev.json.JSONObject
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
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
        auth : Authentication,
        @RequestParam(value = "number", required = false) number: Int,
    ) : ResponseEntity<Any>{

        val accessToken = getUserTokenFromPrincipal(auth)["token"] as String

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
        auth : Authentication,
        @RequestParam(value = "repo", required = true) repoName : String,
    ) : ResponseEntity<Any>{
        val accessToken = getUserTokenFromPrincipal(auth)["token"] as String
        val username = getUserTokenFromPrincipal(auth)["username"] as String

        return ResponseEntity.ok().body(repoService.getAllPagesInRepo(username, accessToken, repoName))
    }

    @GetMapping("/{repo}/{file}")
    fun getSingleFile(
        auth : Authentication,
        @PathVariable file: String,
        @PathVariable repo: String,
    ) : ResponseEntity<Any>{
        val accessToken = getUserTokenFromPrincipal(auth)["token"] as String
        val username = getUserTokenFromPrincipal(auth)["username"] as String

        return ResponseEntity.ok().body(repoService.getContent(
            repo, file, username, accessToken
        ))
    }

    fun getUserTokenFromPrincipal(auth: Authentication) : Map<String, String> {
        val authentication = auth as OAuth2AuthenticationToken
        val user = authentication.principal as CustomOauth2User

        return mapOf(
            "username" to user.getNickname(),
            "token" to user.getToken()
        )
    }
}