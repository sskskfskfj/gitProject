package kotlin1.kopring.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlin1.kopring.Dto.CustomOauth2User
import kotlin1.kopring.Dto.ResponseDto
import kotlin1.kopring.config.jwt.JwtProvider
import mu.KotlinLogging
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class SuccessHandler (
    private val jwtProvider : JwtProvider,
    private val objectMapper: ObjectMapper
) : AuthenticationSuccessHandler{

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        val customUser = authentication?.principal as CustomOauth2User
        val username = customUser.name
        val githubId = customUser.attributes["id"].toString()
        val accessToken = customUser.getToken()

        val jwt : String = jwtProvider.generateToken(githubId, username, accessToken)
        logger.info { "successHandler : $jwt" }

        val responseDto = ResponseDto(
            status = 200,
            message = "로그인 성공 ",
            data = jwt
        )
        val json = objectMapper.writeValueAsString(responseDto) // responseDto -> json 직렬화

        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
        response?.writer?.write(json)
        response?.sendRedirect("/repo/get?number=1")
    }

}