package io.github.jlzhjp.springsecurityjwt.authentication

import org.springframework.security.core.AuthenticationException

class InvalidRefreshTokenException(message: String, cause: Throwable?) : AuthenticationException(message, cause) {
    constructor(message: String) : this(message, null)
}
