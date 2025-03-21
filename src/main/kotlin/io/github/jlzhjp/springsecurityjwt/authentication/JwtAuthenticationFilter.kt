package io.github.jlzhjp.springsecurityjwt.authentication

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.tomcat.websocket.AuthenticationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val authenticationManager: AuthenticationManager
) : OncePerRequestFilter() {
    private val authenticationDetailsSource = WebAuthenticationDetailsSource()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorizationHeader.substring(7)

        try {
            val authenticationRequest = BearerAuthenticationToken(
                token = token,
                details = authenticationDetailsSource.buildDetails(request)
            )
            val authenticationResult = authenticationManager.authenticate(authenticationRequest)
            SecurityContextHolder.getContext().authentication = authenticationResult
        } catch (e: AuthenticationException) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}