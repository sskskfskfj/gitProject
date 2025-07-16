package kotlin1.kopring.config.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import io.jsonwebtoken.security.Keys
import kotlin1.kopring.entity.UserEntity
import mu.KotlinLogging
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class JwtProvider (private val jwtProperties: JwtProperties) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateToken(githubId : String, username : String, token : String) : String {
        val now = Date()
        val expirationDate = Date(now.time + jwtProperties.expiration) // 10분
        return Jwts.builder()
            .setSubject(githubId)
            .claim("username", username)
            .claim("id", githubId)
            .claim("token", token)
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validToken(token: String) : Boolean {
        return try{
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        }catch (e : Exception){
            false
        }
    }

    fun getAllClaims(token : String) : Claims {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body
    }

    fun getGithubId(token : String) : String {
        return getAllClaims(token)["id"].toString()
    }

    fun getAccessToken(token : String) : String {
        return getAllClaims(token)["token"].toString()
    }
}