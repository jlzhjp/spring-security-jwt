package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.authentication.base.UnauthenticatedToken

class RefreshTokenAuthenticationToken(
    val refreshToken: String,
    details: Any
) : UnauthenticatedToken(
    principal = refreshToken,
    credentials = refreshToken,
    details = details
)