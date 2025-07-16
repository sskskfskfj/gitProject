package kotlin1.kopring.service

import jakarta.transaction.Transactional
import kotlin1.kopring.Dto.CustomOauth2User
import kotlin1.kopring.config.jwt.JwtProvider
import kotlin1.kopring.entity.UserEntity
import kotlin1.kopring.repository.UserRepository
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.util.Scanner
import kotlin.math.log

private val logger = KotlinLogging.logger {}

@Service
class CustomOauth2UserService (
    private val userRepository : UserRepository
) : OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest?): OAuth2User? {
        logger.info { "loadUser trigger" }

        val oAuth2User = DefaultOAuth2UserService().loadUser(userRequest)
        val attributes = oAuth2User.attributes
        val accessToken = userRequest?.accessToken?.tokenValue

        logger.info { attributes.entries } // 세부적인거 다보여줌

        val github = attributes["id"].toString()
        val name = attributes["login"].toString()
        val avatar = attributes["avatar_url"].toString()
        val email = attributes["email"]?.toString()?: "email is null"

        val user = userRepository.findByGithubId(github) ?: userRepository.save(
            UserEntity(
                githubId = github,
                username = name,
                email = email,
                avatarUrl = avatar
            )
        )

        return CustomOauth2User(user, attributes, accessToken)
    }
}