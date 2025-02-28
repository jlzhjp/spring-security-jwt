package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import java.util.*

class JwtAuthenticationProvider(private val userRepository: UserRepository) : AuthenticationProvider {
    private val jwtService = JwtService()

    override fun authenticate(authentication: Authentication?): Authentication {
        val bearer = authentication as BearerTokenAuthenticationToken

        val jwt = try {
            jwtService.parseToken(bearer.token)
        } catch (e: JwtException) {
            throw InvalidJwtException("Can not verify jwt", e)
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtException("Invalid JWT token", e)
        } catch (e: Exception) {
            throw InvalidJwtException("Invalid JWT token", e)
        }

        if (jwt.payload.expiration.before(Date(System.currentTimeMillis()))) {
            throw InvalidJwtException("Token expired")
        }

        val id = try {
            UUID.fromString(jwt.payload.subject)
        } catch (e: IllegalArgumentException) {
            throw InvalidJwtException("Invalid user id in jwt subject", e)
        }

        val user = userRepository.findById(id).orElseThrow({
            InvalidJwtException("User not found")
        })

        return JwtAuthentication(user, jwt, user.authorities.toMutableSet())
    }

    override fun supports(authentication: Class<*>): Boolean {
        return BearerTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}