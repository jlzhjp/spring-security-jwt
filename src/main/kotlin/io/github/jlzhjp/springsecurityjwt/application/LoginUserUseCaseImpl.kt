package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.authentication.JwtService
import io.github.jlzhjp.springsecurityjwt.domain.*
import org.springframework.stereotype.Service

@Service
class LoginUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) : LoginUserUseCase {

    private val jwtService = JwtService()

    override fun execute(username: String, userAgent: String): LoginUserUseCase.LoginResult {
        val user = userRepository.findByUsername(username) ?: throw UserNotFoundException("User not found")
        val userAgentInfo = UserAgentInfo(userAgent)
        val session = Session.create(user, userAgentInfo)

        sessionRepository.save(session)

        return LoginUserUseCase.LoginResult(
            jwtService.generateToken(user),
            session.id.toString()
        )
    }
}