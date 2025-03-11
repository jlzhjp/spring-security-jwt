package io.github.jlzhjp.springsecurityjwt.actuator

import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.stereotype.Component
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RequestMatcher

@Component
@Endpoint(id = "securityfilters")
class SecurityFiltersEndpoint(val filterChainProxy: FilterChainProxy) {
    @ReadOperation
    fun securityFilters(): List<Map<String, Any>> {
        val filtersInfo = mutableListOf<Map<String, Any>>()

        filterChainProxy.filterChains.forEachIndexed { chainIndex, chain ->
            val chainInfo = linkedMapOf<String, Any>()

            // Add chain index
            chainInfo["chainIndex"] = chainIndex

            // Add URL pattern information
            val requestMatcher = getRequestMatcher(chain)
            chainInfo["urlPattern"] = requestMatcher?.toString() ?: "unknown pattern"

            // Process filters with more details
            val filters = mutableListOf<Map<String, Any>>()
            chain.filters.forEachIndexed { filterIndex, filter ->
                val filterInfo = mapOf(
                    "index" to filterIndex,
                    "name" to filter.javaClass.simpleName,
                    "class" to filter.javaClass.name
                )
                filters.add(filterInfo)
            }

            chainInfo["filterCount"] = filters.size
            chainInfo["filters"] = filters
            filtersInfo.add(chainInfo)
        }

        return filtersInfo
    }

    private fun getRequestMatcher(chain: SecurityFilterChain): RequestMatcher? {
        return try {
            // Use reflection to access the matcher field
            val field = chain.javaClass.getDeclaredField("requestMatcher")
            field.isAccessible = true
            field.get(chain) as? RequestMatcher
        } catch (e: Exception) {
            null
        }
    }
}

