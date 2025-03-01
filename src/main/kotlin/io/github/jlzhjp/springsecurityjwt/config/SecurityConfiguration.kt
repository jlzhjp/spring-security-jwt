package io.github.jlzhjp.springsecurityjwt.config

import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationFilter
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationProvider
import io.jsonwebtoken.Jwt
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration {

    @Bean
    @Order(1)
    fun tokenUrlSecurityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        http {
            securityMatcher("/api/auth/token")
            authorizeHttpRequests { authorize(anyRequest, authenticated) }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            httpBasic { }
        }

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
        userRepository: UserRepository) : SecurityFilterChain {

        http {
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(JwtAuthenticationFilter(authenticationManager))
        }

        return http.build()
    }

    @Bean
    fun userDetailsService(userRepository: UserRepository) : UserDetailsService {
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
        passwordEncoder: PasswordEncoder): AuthenticationManager
    = ProviderManager(listOf(
        DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailsService)
            setPasswordEncoder(passwordEncoder)
        },
        JwtAuthenticationProvider(userRepository)
    ))
}