package io.github.jlzhjp.springsecurityjwt.authentication.base

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

abstract class UnauthenticatedToken(
    private val principal: Any,
    private val credentials: Any,
    private val details: Any
) : Authentication {
    override fun getPrincipal() = principal
    override fun getCredentials() = credentials
    override fun getDetails() = details

    override fun getName() = throw UnsupportedOperationException()
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()
    override fun isAuthenticated() = false
    override fun setAuthenticated(isAuthenticated: Boolean) {}
}