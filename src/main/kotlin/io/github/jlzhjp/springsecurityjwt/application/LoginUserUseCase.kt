package io.github.jlzhjp.springsecurityjwt.application

interface LoginUserUseCase {
    data class LoginResult(val accessToken: String, val refreshToken: String)

    fun execute(username: String, userAgent: String): LoginResult
}