package io.github.jlzhjp.springsecurityjwt.config

import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationFilter
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationProvider
import io.github.jlzhjp.springsecurityjwt.authentication.RefreshTokenAuthenticationFilter
import io.github.jlzhjp.springsecurityjwt.authentication.RefreshTokenAuthenticationProvider
import io.github.jlzhjp.springsecurityjwt.domain.SessionRepository
import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {
    @Bean
    @Order(1)
    fun refreshTokenUrlSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        http.configureRefreshTokenUrlSecurity {
            addFilterAfter<AnonymousAuthenticationFilter>(RefreshTokenAuthenticationFilter(authenticationManager))
        }
        return http.build()
    }

    @Bean
    @Order(2)
    fun authorizeUrlSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain {
        http.configureAuthorizeUrlSecurity { }
        return http.build()
    }

    @Bean
    @Order(3)
    fun defaultSecurityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager
    ): SecurityFilterChain  {
        http.configureDefaultSecurity {
            addFilterAfter<AnonymousAuthenticationFilter>(RefreshTokenAuthenticationFilter(authenticationManager))
        }
        return http.build()
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService {
        return UserDetailsService { username: String ->
            userRepository.findByUsername(username)
                ?: throw UsernameNotFoundException("User not found")
        }
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    @Primary
    fun authenticationManager(
        userDetailsService: UserDetailsService,
        userRepository: UserRepository,
        sessionRepository: SessionRepository,
        passwordEncoder: PasswordEncoder
    ): AuthenticationManager = ProviderManager(
        listOf(
            DaoAuthenticationProvider().apply {
                setUserDetailsService(userDetailsService)
                setPasswordEncoder(passwordEncoder)
            },
            JwtAuthenticationProvider(userRepository),
            RefreshTokenAuthenticationProvider(sessionRepository)
        )
    )
}