package io.github.jlzhjp.springsecurityjwt.domain

import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    var id: UUID = UUID.randomUUID(),
    @Column(unique = true)
    private var username: String,
    private var password: String,
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
) : UserDetails {
    override fun getAuthorities() =
        roles.map { SimpleGrantedAuthority("ROLE_${it.name.uppercase()}") } +
                roles.flatMap { role ->
                    role.authorities.map { authority ->
                        SimpleGrantedAuthority(authority.name) } }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername() = username
    override fun getPassword() = password
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}