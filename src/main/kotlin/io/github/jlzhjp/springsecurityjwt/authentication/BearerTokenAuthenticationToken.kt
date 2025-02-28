package io.github.jlzhjp.springsecurityjwt.authentication

import org.springframework.security.authentication.AbstractAuthenticationToken

class BearerTokenAuthenticationToken(val token: String) : AbstractAuthenticationToken(emptyList()) {
    override fun getCredentials() = token
    override fun getPrincipal() = token
}