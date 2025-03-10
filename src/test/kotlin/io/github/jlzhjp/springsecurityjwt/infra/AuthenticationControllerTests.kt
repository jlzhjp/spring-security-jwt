package io.github.jlzhjp.springsecurityjwt.infra

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.jlzhjp.springsecurityjwt.application.LoginUserUseCase
import io.github.jlzhjp.springsecurityjwt.application.RefreshTokenUseCase
import io.github.jlzhjp.springsecurityjwt.application.RegisterUserUseCase
import io.github.jlzhjp.springsecurityjwt.authentication.AuthenticationConstants
import io.github.jlzhjp.springsecurityjwt.authentication.JwtAuthentication
import io.github.jlzhjp.springsecurityjwt.authentication.RefreshTokenAuthentication
import io.github.jlzhjp.springsecurityjwt.config.SecurityConfiguration
import io.github.jlzhjp.springsecurityjwt.domain.RoleNameNotFoundException
import io.github.jlzhjp.springsecurityjwt.domain.Session
import io.github.jlzhjp.springsecurityjwt.domain.User
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(AuthenticationController::class)
@Import(SecurityConfiguration::class)
class AuthenticationControllerTests {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var loginUserUseCase: LoginUserUseCase

    @Autowired
    private lateinit var registerUserUseCase: RegisterUserUseCase

    @Autowired
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @TestConfiguration
    class TestConfig {
        @Bean
        fun loginUserUseCase() = mockk<LoginUserUseCase>()

        @Bean
        fun registerUserUseCase() = mockk<RegisterUserUseCase>()

        @Bean
        fun refreshTokenUseCase() = mockk<RefreshTokenUseCase>()
    }

    @Test
    fun `should register user successfully`() {
        val registerRequest = RegisterRequest(
            username = "testuser",
            password = "password123",
            role = "USER"
        )

        val registerResult = RegisterUserUseCase.RegisterResult(
            accessToken = "access-token-123",
            refreshToken = "refresh-token-456"
        )

        every {
            registerUserUseCase.execute(
                username = eq("testuser"),
                password = eq("password123"),
                role = eq("USER"),
                userAgent = any()
            )
        } returns registerResult

        mockMvc.post("/api/auth/register") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("access-token-123") }
            cookie { value(AuthenticationConstants.REFRESH_TOKEN_COOKIE, "refresh-token-456") }
        }
    }

    @Test
    fun `should return bad request when role not found`() {
        // Given
        val registerRequest = RegisterRequest(
            username = "testuser",
            password = "password123",
            role = "INVALID_ROLE"
        )

        every {
            registerUserUseCase.execute(
                username = eq("testuser"),
                password = eq("password123"),
                role = eq("INVALID_ROLE"),
                userAgent = any()
            )
        } throws RoleNameNotFoundException("INVALID_ROLE")

        mockMvc.post("/api/auth/register") {
            with(csrf())
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value("Role INVALID_ROLE not found") }
        }
    }

    @Test
    fun `should generate token for authenticated user`() {
        // Given
        val user = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded-password",
            roles = mutableSetOf()
        )

        val result = LoginUserUseCase.LoginResult(
            accessToken = "new-access-token",
            refreshToken = "new-refresh-token"
        )

        every {
            loginUserUseCase.execute(eq("testuser"), any())
        } returns result


        mockMvc.post("/api/auth/authorize") {
            with(csrf())
            with(authentication(UsernamePasswordAuthenticationToken(user, null, mutableListOf())))
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("new-access-token") }
        }
    }

    @Test
    fun `should refresh token for authenticated user`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded-password",
            roles = mutableSetOf()
        )

        val session = Session(
            id = UUID.randomUUID(),
            user = user,
            agent = "",
            agentVersion = "",
            os = "",
            osVersion = "",
            device = "",
        )

        every {
            refreshTokenUseCase.execute(user = any())
        } returns "refreshed-access-token"

        mockMvc.post("/api/auth/token/refresh") {
            with(csrf())
            with(authentication(RefreshTokenAuthentication(session)))
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("refreshed-access-token") }
        }
    }

    @Test
    fun `should return user info for authenticated user`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "testuser",
            password = "encoded-password",
            roles = mutableSetOf()
        )

        mockMvc.get("/api/auth/me") {
            with(csrf())
            with(authentication(JwtAuthentication(user, user.authorities.toMutableList(), Any())))
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(user.id.toString()) }
            jsonPath("$.username") { value(user.username) }
        }
    }

    @Test
    fun `should deny access to me endpoint for unauthenticated users`() {
        mockMvc.get("/api/auth/me") {

        }.andExpect {
            status { isUnauthorized() }
        }
    }
}
