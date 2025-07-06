package kotlin1.kopring.config.jwt

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt")
class JwtProperties {
    val secret: String = dotenv()["JWT_SECRET"]!!
    val expiration : Long = 1000 * 60 * 10
}