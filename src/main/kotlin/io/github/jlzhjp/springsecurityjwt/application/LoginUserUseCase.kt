package io.github.jlzhjp.springsecurityjwt.application

data class LoginResult(val accessToken: String, val refreshToken: String)

interface LoginUserUseCase {
    fun execute(username: String, userAgent: String): LoginResult
}