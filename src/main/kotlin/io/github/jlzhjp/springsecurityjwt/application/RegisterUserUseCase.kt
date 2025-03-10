package io.github.jlzhjp.springsecurityjwt.application

interface RegisterUserUseCase {
    data class RegisterResult(val accessToken: String, val refreshToken: String)

    fun execute(username: String, password: String, role: String, userAgent: String): RegisterResult
}