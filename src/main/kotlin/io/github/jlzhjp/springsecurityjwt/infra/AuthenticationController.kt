package io.github.jlzhjp.springsecurityjwt.infra

import io.github.jlzhjp.springsecurityjwt.application.RegisterUserUseCase
import io.github.jlzhjp.springsecurityjwt.authentication.JwtService
import io.github.jlzhjp.springsecurityjwt.domain.RoleNameNotFoundException
import io.github.jlzhjp.springsecurityjwt.domain.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/auth")
class AuthenticationController(
    val registerUserUseCase: RegisterUserUseCase
) {
    val jwtService = JwtService()

    data class RegisterRequest(
        val username: String,
        val password: String,
        val roles: List<String>
    )

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest) : ResponseEntity<Any> {
        try {
            registerUserUseCase.register(
                username = request.username,
                password = request.password,
                roles = request.roles
            )
        } catch (e: RoleNameNotFoundException) {
            return ResponseEntity.badRequest().body(ApiError(e.message ?: "Role not found"))
        }
        return ResponseEntity.ok().build()
    }

    data class TokenResponse(val token: String)

    @PostMapping("/token")
    fun token(@AuthenticationPrincipal principal: Principal) = TokenResponse(
        token = jwtService.generateToken(principal as User, emptyMap())
    )
}