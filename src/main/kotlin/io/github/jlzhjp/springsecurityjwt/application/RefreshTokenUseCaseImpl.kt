package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.authentication.JwtService
import io.github.jlzhjp.springsecurityjwt.domain.User
import org.springframework.stereotype.Service

@Service
class RefreshTokenUseCaseImpl : RefreshTokenUseCase {
    private val jwtService = JwtService()

    override fun execute(user: User): String {
        return jwtService.generateToken(user)
    }
}