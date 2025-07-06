package kotlin1.kopring.config.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin1.kopring.Dto.CustomOauth2User
import kotlin1.kopring.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

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
        val authHeader = request.getHeader("Authorization")
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7).trim()
            if(jwtProvider.validToken(token)){
                val githubId = jwtProvider.getGithubId(token)
                val user = userRepository.findByGithubId(githubId)?: throw UsernameNotFoundException("User not found")
                val accessToken = jwtProvider.getAccessToken(token)

                val userAttribute : Map<String, Any> = mapOf(
                    "githubId" to user.githubId,
                    "username" to user.username,
                    "email" to user.email!!
                )
                val oAuth2User = CustomOauth2User(user, userAttribute, accessToken)
                val auth = OAuth2AuthenticationToken(
                    oAuth2User,
                    oAuth2User.authorities,
                    "github"
                )
                SecurityContextHolder.getContext().authentication = auth
            }
        }
        filterChain.doFilter(request, response)
    }
}