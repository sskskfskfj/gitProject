package kotlin1.kopring.repository

import kotlin1.kopring.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
    fun findByGithubId(githubId: String): UserEntity?
}