package io.github.jlzhjp.springsecurityjwt.config

import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.HttpSecurityDsl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.authentication.HttpStatusEntryPoint


fun HttpSecurity.configureRefreshTokenUrlSecurity(extraConfiguration: HttpSecurityDsl.() -> Unit) {
    this {
        securityMatcher("/api/auth/token/refresh")
        authorizeHttpRequests { authorize(anyRequest, authenticated) }
        sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
        csrf { disable() }
        anonymous { disable() }
        extraConfiguration.invoke(this)
    }
}

fun HttpSecurity.configureAuthorizeUrlSecurity(extraConfiguration: HttpSecurityDsl.() -> Unit) {
    this {
        securityMatcher("/api/auth/authorize")
        authorizeHttpRequests { authorize(anyRequest, authenticated) }
        sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
        csrf { disable() }
        httpBasic { }
        anonymous { disable() }
        extraConfiguration.invoke(this)
    }
}

fun HttpSecurity.configureDefaultSecurity(extraConfiguration: HttpSecurityDsl.() -> Unit) {
    this {
        authorizeHttpRequests {
            authorize("/api/auth/register", permitAll)
            authorize("/api/auth/me", authenticated)
            authorize(anyRequest, permitAll)
        }
        sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
        csrf { disable() }
        anonymous { disable() }
        exceptionHandling {
            authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
        }
        extraConfiguration.invoke(this)
    }
}
