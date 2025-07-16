package kotlin1.kopring.controller

import kotlin1.kopring.service.RepoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AIController (
    private val repoService : RepoService,
    private val repoController : RepoController
){
    @GetMapping("/ai/{fileName}")
    fun getAIResponse(
        @PathVariable fileName: String,
    ) : ResponseEntity<Any> {
        val token = repoController.getUserTokenFromPrincipal()["token"] as String
        // todo: gateway

        return ResponseEntity.ok().body(200)
    }
}