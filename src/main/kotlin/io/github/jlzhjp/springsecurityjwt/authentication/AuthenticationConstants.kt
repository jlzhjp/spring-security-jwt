package io.github.jlzhjp.springsecurityjwt.authentication

class AuthenticationConstants {
    companion object {
        const val REFRESH_TOKEN_COOKIE = "RefreshToken"
        const val AUTHORIZE_ENDPOINT = "/api/auth/authorize"
        const val REFRESH_TOKEN_ENDPOINT = "/api/auth/token/refresh"
    }
}