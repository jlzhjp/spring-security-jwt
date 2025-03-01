package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.domain.User

interface RegisterUserUseCase {
    fun register(username: String, password: String, role: String): User
}