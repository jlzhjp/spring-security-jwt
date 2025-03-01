package io.github.jlzhjp.springsecurityjwt.infra

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@Profile("dev")
@RequestMapping("/{path:^(?!api).*}", "/{path:^(?!api).*}/**")
class DevFallbackController {
    @GetMapping
    fun forward(@PathVariable path: String, response: HttpServletResponse) {
        response.setHeader("Location", "http://localhost:5173/$path")
        response.status = HttpStatus.FOUND.value()
    }
}