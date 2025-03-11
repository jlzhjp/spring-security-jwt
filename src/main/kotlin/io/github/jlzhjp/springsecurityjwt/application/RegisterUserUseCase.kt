package io.github.jlzhjp.springsecurityjwt.application

data class RegisterResult(val accessToken: String, val refreshToken: String)

interface RegisterUserUseCase {
    fun execute(username: String, password: String, role: String, userAgent: String): RegisterResult
}