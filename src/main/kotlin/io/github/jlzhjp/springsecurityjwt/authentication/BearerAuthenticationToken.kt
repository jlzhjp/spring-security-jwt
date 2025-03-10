package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.authentication.base.UnauthenticatedToken

class BearerAuthenticationToken(
    val token: String,
    details: Any
) : UnauthenticatedToken(principal = token, credentials = token, details = details)