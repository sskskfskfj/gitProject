package kotlin1.kopring.controller

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController {

    @GetMapping("/github")
    fun testingOAuth2 (@AuthenticationPrincipal oAuth2User : OAuth2User?) : Map<String, String?> {
            return mapOf(
                "name" to oAuth2User?.getAttribute<String>("login"),
                "avatarUrl" to oAuth2User?.getAttribute<String>("avatar_url")
            )
    }

    ///oauth2/authorization/github
}