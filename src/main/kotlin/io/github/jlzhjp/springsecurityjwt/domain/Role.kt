package io.github.jlzhjp.springsecurityjwt.domain

import jakarta.persistence.*
import java.util.*

@Entity
class Role(
    @Id
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_authority",
        joinColumns = [JoinColumn(name = "role_id")],
        inverseJoinColumns = [JoinColumn(name = "authority_id")]
    )
    var authorities: MutableSet<Authority> = mutableSetOf()
)