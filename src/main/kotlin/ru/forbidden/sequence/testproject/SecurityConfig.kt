package ru.forbidden.sequence.testproject

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
internal class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/cashcards/**")
                    .hasRole("CARD-OWNER")
            }.httpBasic(Customizer.withDefaults())
            .csrf { csrf -> csrf.disable() }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun testOnlyUsers(passwordEncoder: PasswordEncoder): UserDetailsService {
        val userBuilder = User.builder()
        val sarah: UserDetails =
            userBuilder
                .username("sarah1")
                .password(passwordEncoder.encode("abc123"))
                .roles("CARD-OWNER")
                .build()
        val sasha: UserDetails =
            userBuilder
                .username("sasha")
                .password(passwordEncoder.encode("sasha_the_best234567890"))
                .roles("CARD-OWNER")
                .build()
        val hank =
            userBuilder
                .username("hank")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER")
                .build()
        val kumar: UserDetails =
            userBuilder
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build()
        return InMemoryUserDetailsManager(sarah, hank, sasha, kumar)
    }
}
