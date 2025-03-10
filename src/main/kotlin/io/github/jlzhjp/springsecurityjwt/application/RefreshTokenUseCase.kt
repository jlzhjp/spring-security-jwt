package io.github.jlzhjp.springsecurityjwt.application

import io.github.jlzhjp.springsecurityjwt.domain.User

interface RefreshTokenUseCase {
    fun execute(user: User): String
}