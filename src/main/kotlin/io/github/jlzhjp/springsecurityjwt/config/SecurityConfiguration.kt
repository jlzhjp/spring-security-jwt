package io.github.jlzhjp.springsecurityjwt.config

import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationFilter
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthenticationProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke;
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(
    val authenticationConfiguration: AuthenticationConfiguration,
    val jwtAuthenticationFilter: JwtAuthenticationFilter) {

    @Bean
    @Order(1)
    fun tokenUrlSecurityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        http {
            securityMatcher("/auth/token")
            authorizeHttpRequests { authorize(anyRequest, authenticated) }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            httpBasic { }
        }

        return http.build()
    }

    @Bean
    @Order(2)
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/auth/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
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
    fun jwtAuthenticationProvider(userRepository: UserRepository) = JwtAuthenticationProvider(userRepository)

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(): AuthenticationManager = authenticationConfiguration.authenticationManager
}