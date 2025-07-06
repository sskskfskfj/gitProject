package kotlin1.kopring.Dto

import kotlin1.kopring.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomOauth2User(
    private val userEntity: UserEntity,
    private val attributes: Map<String, Any>,
    private val accessToken: String?
) : OAuth2User{

    override fun getName(): String = userEntity.username
    override fun getAttributes(): Map<String, Any> = attributes
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority("ROLE_USER"))
    }
    fun getToken() : String = accessToken ?: ""

    fun getId(): Long = (attributes["id"] as Number).toLong()

    fun getNickname(): String = attributes["nickname"].toString()

    fun getTokenSubject(): String = attributes["token_subject"].toString()
}