package kotlin1.kopring.config.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin1.kopring.Dto.CustomOauth2User
import org.springframework.http.HttpHeaders
import kotlin1.kopring.repository.UserRepository
import mu.KotlinLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class JwtFilter (
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        logger.info("jwt filter trigger")
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if(header != null && header.startsWith("Bearer ")){
            val token = header.substring(7).trim()
            logger.info(jwtProvider.getGithubId(token))

            if(jwtProvider.validToken(token)){
                val githubId = jwtProvider.getGithubId(token)

                val user = userRepository.findByGithubId(githubId)
                val attributes = mapOf(
                    "username" to user!!.username,
                    "githubId" to githubId,
                )

                val oAuth2User = CustomOauth2User(user, attributes, jwtProvider.getAccessToken(token))
                val authenticatedUser = OAuth2AuthenticationToken(
                    oAuth2User,
                    oAuth2User.authorities,
                    "github"
                )

                SecurityContextHolder.getContext().authentication = authenticatedUser
            }
        }
        filterChain.doFilter(request, response)
    }


}