package io.github.jlzhjp.springsecurityjwt.authentication.base

import org.springframework.security.core.AuthenticatedPrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Principal

abstract class SucceededAuthentication(
    private val principal: Any,
    private val authorities: MutableCollection<out GrantedAuthority>,
    private val details: Any
) : Authentication {
    override fun getName(): String = when (principal) {
        is UserDetails -> principal.username
        is AuthenticatedPrincipal -> principal.name
        is Principal -> principal.name
        else -> principal.toString()
    }

    override fun getAuthorities() = authorities
    override fun getDetails() = details
    override fun getPrincipal() = principal
    override fun getCredentials() = null
    override fun isAuthenticated() = true
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}