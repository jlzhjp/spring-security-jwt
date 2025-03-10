package io.github.jlzhjp.springsecurityjwt.domain

class RoleNameNotFoundException(role: String) : Exception("Role ${role} not found")