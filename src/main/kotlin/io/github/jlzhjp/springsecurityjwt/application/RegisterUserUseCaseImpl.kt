package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.domain.RoleNameNotFoundException
import io.github.jlzhjp.springsecurityjwt.domain.RoleRepository
import io.github.jlzhjp.springsecurityjwt.domain.User
import io.github.jlzhjp.springsecurityjwt.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegisterUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) : RegisterUserUseCase {
    override fun register(username: String, password: String, role: String): User {
        val encodedPassword = passwordEncoder.encode(password)
        val roleObject = roleRepository.findByNameEqualsIgnoreCase(role) ?: throw RoleNameNotFoundException("Role not found")
        val user = User(UUID.randomUUID(), username, encodedPassword, mutableSetOf(roleObject))
        userRepository.save(user)
        return user
    }
}
