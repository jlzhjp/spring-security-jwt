package io.github.jlzhjp.springsecurityjwt.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
class Session(
    @Id
    val id: UUID = UUID.randomUUID(),
    @ManyToOne
    val user: User,
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val device: String,
    val agent: String,
    val agentVersion: String,
    val os: String,
    val osVersion: String,
) {
    companion object {
        fun create(user: User, userAgentInfo: UserAgentInfo): Session {
            return Session(
                user = user,
                device = userAgentInfo.device,
                agent = userAgentInfo.agent,
                agentVersion = userAgentInfo.agentVersion,
                os = userAgentInfo.os,
                osVersion = userAgentInfo.osVersion
            )
        }
    }
}