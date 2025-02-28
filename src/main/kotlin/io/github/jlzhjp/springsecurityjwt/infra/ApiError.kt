package io.github.jlzhjp.springsecurityjwt.infra

data class ApiError(val message: String, val timestamp: Long) {
    constructor(message: String) : this(message, System.currentTimeMillis())
}