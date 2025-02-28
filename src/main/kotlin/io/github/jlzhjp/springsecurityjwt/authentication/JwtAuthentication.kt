package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.domain.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthentication(
    private val principal: User,
    private val credentials: Jws<Claims>,
    authorities: MutableCollection<out GrantedAuthority>?) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return credentials
    }

    override fun getPrincipal(): Any {
        return principal
    }
}