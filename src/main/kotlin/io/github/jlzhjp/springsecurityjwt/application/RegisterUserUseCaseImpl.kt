package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.authentication.JwtService
import io.github.jlzhjp.springsecurityjwt.domain.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class RegisterUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder,
    private val sessionRepository: SessionRepository
) : RegisterUserUseCase {
    private val jwtService = JwtService()

    override fun execute(
        username: String,
        password: String,
        role: String,
        userAgent: String
    ): RegisterUserUseCase.RegisterResult {

        val encodedPassword = passwordEncoder.encode(password)
        val roleObject =
            roleRepository.findByNameEqualsIgnoreCase(role) ?: throw RoleNameNotFoundException(role)
        val user = User(UUID.randomUUID(), username, encodedPassword, mutableSetOf(roleObject))

        userRepository.save(user)

        val userAgentInfo = UserAgentInfo(userAgent)

        val session = Session.create(user, userAgentInfo)
        sessionRepository.save(session)

        val token = jwtService.generateToken(user)

        return RegisterUserUseCase.RegisterResult(
            accessToken = token,
            refreshToken = session.id.toString()
        )
    }
}
