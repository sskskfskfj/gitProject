package kotlin1.kopring.config

import io.jsonwebtoken.Jwt
import kotlin1.kopring.config.jwt.JwtFilter
import kotlin1.kopring.handler.SuccessHandler
import kotlin1.kopring.service.CustomOauth2UserService
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

private val logger = KotlinLogging.logger {}

@Configuration
@EnableWebSecurity
class SecurityConfig (
    private val customOauth2UserService: CustomOauth2UserService,
    private val successHandler: SuccessHandler,
    private val jwtFilter: JwtFilter,
){

    @Bean
    fun securityfilterchain(http : HttpSecurity) : SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/oauth2/**").permitAll()
                it.anyRequest().authenticated()
            }.oauth2Login { it ->
                it.failureHandler { _, _, exception ->
                    logger.error { "oauth2 login failed : ${exception.localizedMessage}" }
                }.userInfoEndpoint {
                    it.userService(customOauth2UserService)
                }.successHandler(successHandler)
            }.exceptionHandling{
                it.authenticationEntryPoint { _, response, _ ->
                response.sendRedirect("/oauth2/authorization/github")
                }
            }.sessionManagement{ s ->
                s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

}