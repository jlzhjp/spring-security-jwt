package io.github.jlzhjp.springsecurityjwt.domain

import nl.basjes.parse.useragent.UserAgentAnalyzer

class UserAgentInfo(userAgent: String) {
    val device: String
    val os: String
    val osVersion: String
    val agent: String
    val agentVersion: String

    init {
        val uaa = UserAgentAnalyzer.newBuilder().hideMatcherLoadStats().build()
        val parseResult = uaa.parse(userAgent)
        device = parseResult.getValue("Device Class") ?: "Unknown"
        os = parseResult.getValue("Operating System Name") ?: "Unknown"
        osVersion = parseResult.getValue("Operating System Version") ?: "Unknown"
        agent = parseResult.getValue("Agent Name") ?: "Unknown"
        agentVersion = parseResult.getValue("Agent Version") ?: "Unknown"
    }
}