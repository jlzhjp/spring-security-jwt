package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.authentication.base.SucceededAuthentication
import io.github.jlzhjp.springsecurityjwt.domain.User
import org.springframework.security.core.GrantedAuthority

class JwtAuthentication(
    principal: User,
    authorities: MutableCollection<out GrantedAuthority>,
    details: Any
) : SucceededAuthentication(
    principal = principal,
    authorities = authorities,
    details = details
)