package corp.lolcheck.app.auth.config

import JwtAuthenticationManager
import JwtServerAuthenticationConverter
import corp.lolcheck.app.auth.exception.CustomAccessDeniedHandler
import corp.lolcheck.app.auth.exception.CustomAuthenticationEntryPoint
import corp.lolcheck.app.auth.service.JwtService
import corp.lolcheck.app.auth.service.UserDetailService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtService: JwtService,
    private val userDetailService: UserDetailService
) {

    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
    ): SecurityWebFilterChain {
        val filter = AuthenticationWebFilter(reactiveAuthenticationManager())
        filter.setServerAuthenticationConverter(serverAuthenticationConverter())

        return http
            .httpBasic { it.disable() }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .sessionManagement { it.ConcurrentSessionsSpec() }
            .headers { it.frameOptions { it.disable() } }
            .authorizeExchange {
                it.pathMatchers(HttpMethod.POST, "/api/v1/auth/**").permitAll()
                it.pathMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                it.anyExchange().authenticated()
            }
            .exceptionHandling {
                it.accessDeniedHandler(accessDeniedHandler())
                it.authenticationEntryPoint(authenticationEntryPoint())
            }
            .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder();

    @Bean
    fun reactiveAuthenticationManager(
    ): ReactiveAuthenticationManager {
        return JwtAuthenticationManager(jwtService, userDetailService)
    }

    @Bean
    fun serverAuthenticationConverter(): ServerAuthenticationConverter {
        return JwtServerAuthenticationConverter()
    }

    @Bean
    fun authenticationEntryPoint(): ServerAuthenticationEntryPoint {
        return CustomAuthenticationEntryPoint()
    }

    @Bean
    fun accessDeniedHandler(): CustomAccessDeniedHandler {
        return CustomAccessDeniedHandler()
    }
}