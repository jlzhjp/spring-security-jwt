package io.github.jlzhjp.springsecurityjwt.authentication

import org.apache.tomcat.websocket.AuthenticationException

class InvalidJwtException(message: String) : AuthenticationException(message)