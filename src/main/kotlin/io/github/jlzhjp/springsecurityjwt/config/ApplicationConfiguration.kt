package io.github.jlzhjp.springsecurityjwt.config

import io.github.jlzhjp.springsecurityjwt.domain.Role
import io.github.jlzhjp.springsecurityjwt.domain.RoleRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {
    @Bean
    fun applicationRunner(roleRepository: RoleRepository): ApplicationRunner {
        return ApplicationRunner {
            roleRepository.saveAll(
                listOf(
                    Role(name = "USER"),
                    Role(name = "ADMIN")
                )
            )
        }
    }
}