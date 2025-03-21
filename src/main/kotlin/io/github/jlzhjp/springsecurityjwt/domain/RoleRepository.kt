package io.github.jlzhjp.springsecurityjwt.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, UUID> {
    fun findByName(name: String): Role?
    fun findByNameEqualsIgnoreCase(name: String): Role?
}