package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.domain.SessionRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import java.util.*

class RefreshTokenAuthenticationProvider(
    private val sessionRepository: SessionRepository
) : AuthenticationProvider {
    override fun authenticate(authentication: Authentication?): Authentication {
        val refreshToken = authentication as RefreshTokenAuthenticationToken
        val id = try {
            UUID.fromString(refreshToken.refreshToken)
        } catch (e: IllegalArgumentException) {
            throw InvalidRefreshTokenException("Invalid refresh token")
        }

        val session = sessionRepository.findById(id).orElseThrow {
            InvalidRefreshTokenException("Session not found")
        }

        return RefreshTokenAuthentication(session)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return RefreshTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}