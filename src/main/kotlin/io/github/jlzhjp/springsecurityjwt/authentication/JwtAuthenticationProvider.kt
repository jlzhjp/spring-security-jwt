package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import java.util.*

/**
 * Authentication provider that validates JWT tokens
 */
class JwtAuthenticationProvider(
    private val userRepository: UserRepository,
    private val jwtService: JwtService = JwtService()
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        authentication ?: throw InvalidJwtException("Authentication object cannot be null")

        if (authentication !is BearerAuthenticationToken) {
            throw InvalidJwtException("Authentication must be a BearerAuthenticationToken")
        }

        val jwt = try {
            jwtService.parseToken(authentication.token)
        } catch (e: Exception) {
            when (e) {
                is JwtException -> throw InvalidJwtException("Cannot verify JWT")
                else -> throw InvalidJwtException("Invalid JWT token")
            }
        }

        // Check token expiration
        if (jwt.payload.expiration.before(Date(System.currentTimeMillis()))) {
            throw InvalidJwtException("Token expired")
        }

        // Extract and validate user ID
        val userId = try {
            UUID.fromString(jwt.payload.subject)
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtException("Invalid user id in JWT subject")
        }

        // Look up the user
        val user = userRepository.findById(userId)
            .orElseThrow { InvalidJwtException("User not found") }

        return JwtAuthentication(user, user.authorities.toMutableSet(), authentication.token)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BearerAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}