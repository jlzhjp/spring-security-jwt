package io.github.jlzhjp.springsecurityjwt.infra

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@Profile("prod")
@RequestMapping("/{path:^(?!api).*}", "/{path:^(?!api).*}/**")
class ProdFallbackController {
    @RequestMapping
    fun index(@PathVariable path: String): String {
        return "forward:/"
    }
}
