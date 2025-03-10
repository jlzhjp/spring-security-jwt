package io.github.jlzhjp.springsecurityjwt.infra

import io.github.jlzhjp.springsecurityjwt.application.LoginUserUseCase
import io.github.jlzhjp.springsecurityjwt.application.RefreshTokenUseCase
import io.github.jlzhjp.springsecurityjwt.application.RegisterUserUseCase
import io.github.jlzhjp.springsecurityjwt.authentication.AuthenticationConstants
import io.github.jlzhjp.springsecurityjwt.domain.RoleNameNotFoundException
import io.github.jlzhjp.springsecurityjwt.domain.User
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

/**
 * Data class for user registration request
 */
data class RegisterRequest(
    val username: String,
    val password: String,
    val role: String
)

/**
 * Data class for access token response
 */
data class AccessTokenResult(val accessToken: String, val time: Long = System.currentTimeMillis())

/**
 * Data class for user information response
 */
data class UserInfo(val id: String, val username: String)

/**
 * Controller handling authentication related operations
 */
@RestController
@RequestMapping
class AuthenticationController(
    val loginUserUseCase: LoginUserUseCase,
    val registerUserUseCase: RegisterUserUseCase,
    val refreshTokenUseCase: RefreshTokenUseCase
) {
    /**
     * Registers a new user
     */
    @PostMapping("/api/auth/register")
    fun register(
        @RequestBody request: RegisterRequest,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<Any> {
        val loginResult = try {
            registerUserUseCase.execute(
                username = request.username,
                password = request.password,
                role = request.role,
                userAgent = httpServletRequest.getHeader("User-Agent") ?: ""
            )
        } catch (e: RoleNameNotFoundException) {
            return ResponseEntity.badRequest().body(ApiError(e.message ?: "Role not found"))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(ApiError("Registration failed: ${e.message}"))
        }

        val cookie = Cookie(AuthenticationConstants.REFRESH_TOKEN_COOKIE, loginResult.refreshToken).apply {
            isHttpOnly = true
            secure = true
            path = "/api/auth"
        }
        httpServletResponse.addCookie(cookie)

        return ResponseEntity.ok(AccessTokenResult(loginResult.accessToken))
    }

    /**
     * Generates a new access token for an authenticated user
     */
    @PostMapping(AuthenticationConstants.AUTHORIZE_ENDPOINT)
    @PreAuthorize("isAuthenticated()")
    fun generateAccessToken(
        @AuthenticationPrincipal principal: User,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<AccessTokenResult> {
        return try {
            val result = loginUserUseCase.execute(
                username = principal.username,
                userAgent = httpServletRequest.getHeader("User-Agent") ?: ""
            )
            ResponseEntity.ok(AccessTokenResult(accessToken = result.accessToken))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * Refreshes an access token using a refresh token
     */
    @PostMapping(AuthenticationConstants.REFRESH_TOKEN_ENDPOINT)
    @PreAuthorize("isAuthenticated()")
    fun refreshToken(@AuthenticationPrincipal user: User): ResponseEntity<AccessTokenResult> {
        return try {
            val refreshedToken = refreshTokenUseCase.execute(user)
            ResponseEntity.ok(AccessTokenResult(refreshedToken))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * Returns information about the authenticated user
     */
    @GetMapping("/api/auth/me")
    @PreAuthorize("isAuthenticated()")
    fun getUserInfo(@AuthenticationPrincipal principal: User): ResponseEntity<UserInfo> {
        return ResponseEntity.ok(
            UserInfo(
                id = principal.id.toString(),
                username = principal.username
            )
        )
    }
}