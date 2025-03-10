package io.github.jlzhjp.springsecurityjwt.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
class Authority(
    @Id
    var id: UUID = UUID.randomUUID(),
    var name: String
)