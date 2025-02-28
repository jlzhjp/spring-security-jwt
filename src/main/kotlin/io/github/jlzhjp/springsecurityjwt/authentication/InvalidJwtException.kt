package io.github.jlzhjp.springsecurityjwt.authentication

import org.apache.tomcat.websocket.AuthenticationException

class InvalidJwtException(message: String, cause: Throwable?) : AuthenticationException(message) {
    constructor(message: String) : this(message, null)
}