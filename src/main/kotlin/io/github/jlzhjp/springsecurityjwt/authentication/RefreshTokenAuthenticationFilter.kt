package io.github.jlzhjp.springsecurityjwt.authentication

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter for handling authentication using refresh tokens
 */
class RefreshTokenAuthenticationFilter(
    private val authenticationManager: AuthenticationManager,
) : OncePerRequestFilter() {
    private val authenticationDetailsSource = WebAuthenticationDetailsSource()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val refreshToken = extractRefreshToken(request)

        if (refreshToken == null) {
            logger.debug("No refresh token found in request")
            filterChain.doFilter(request, response)
            return
        }

        try {
            logger.debug("Attempting to authenticate with refresh token")
            val authenticationRequest = RefreshTokenAuthenticationToken(
                refreshToken = refreshToken,
                details = authenticationDetailsSource.buildDetails(request)
            )
            val authenticationResult = authenticationManager.authenticate(authenticationRequest)
            SecurityContextHolder.getContext().authentication = authenticationResult
            logger.debug("Successfully authenticated using refresh token")
        } catch (e: AuthenticationException) {
            logger.warn("Refresh token authentication failed", e)
            SecurityContextHolder.clearContext()
        } catch (e: Exception) {
            logger.error("Unexpected error during refresh token authentication", e)
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    /**
     * Extracts the refresh token from cookies in the request
     */
    private fun extractRefreshToken(request: HttpServletRequest): String? {
        return request.cookies?.find {
            it.name == AuthenticationConstants.REFRESH_TOKEN_COOKIE
        }?.value
    }
}