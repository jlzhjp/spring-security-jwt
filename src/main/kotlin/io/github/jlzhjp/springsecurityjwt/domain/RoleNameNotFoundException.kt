package io.github.jlzhjp.springsecurityjwt.domain

class RoleNameNotFoundException(role: String) : Exception("Role with name ${role} not found")