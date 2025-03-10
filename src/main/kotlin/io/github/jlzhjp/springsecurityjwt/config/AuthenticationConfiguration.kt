package io.github.jlzhjp.springsecurityjwt.config

import io.github.jlzhjp.springsecurityjwt.authentication.*
import io.github.jlzhjp.springsecurityjwt.domain.SessionRepository
import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class AuthenticationConfiguration {
    @Bean
    fun jwtAuthenticationFilter(
        authenticationManager: AuthenticationManager
    ): FilterRegistrationBean<JwtAuthenticationFilter> {
        val filter = JwtAuthenticationFilter(authenticationManager)
        val filterRegistration = FilterRegistrationBean(filter).apply {
            order = -1
            urlPatterns = listOf("/api/**")
        }

        filterRegistration.order = -1
        return filterRegistration
    }

    @Bean
    fun refreshTokenAuthenticationFilter(
        authenticationManager: AuthenticationManager
    ): FilterRegistrationBean<RefreshTokenAuthenticationFilter> {
        val filter = RefreshTokenAuthenticationFilter(authenticationManager)
        val filterRegistration = FilterRegistrationBean(filter).apply {
            order = -1
            urlPatterns = listOf(AuthenticationConstants.REFRESH_TOKEN_ENDPOINT)
        }
        return filterRegistration
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