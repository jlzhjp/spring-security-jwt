package io.github.jlzhjp.springsecurityjwt.domain

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID>{
    fun findByUsername(username: String): User?
}