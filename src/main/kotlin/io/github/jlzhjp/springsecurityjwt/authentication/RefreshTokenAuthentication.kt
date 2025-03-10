package io.github.jlzhjp.springsecurityjwt.authentication

import io.github.jlzhjp.springsecurityjwt.authentication.base.SucceededAuthentication
import io.github.jlzhjp.springsecurityjwt.domain.Session

class RefreshTokenAuthentication(
    session: Session
) : SucceededAuthentication(
    session.user,
    session.user.authorities.toMutableList(),
    session
)