package com.example.es_springboot_security_basis.config;

import com.example.es_springboot_security_basis.security.JwtConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Необходим для работы @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Аутентификация   - (Ошибка 401) Разграничение на друзей и врагов
    // Авторизация      - (Ошибка 403) К каким страницам и каким ресурсам человек имеет доступ

    private final JwtConfigurer jwtConfigurer;

    public SecurityConfig(JwtConfigurer jwtConfigurer) {
        this.jwtConfigurer = jwtConfigurer;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Отключение csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // не используем сессии
                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll() // Доступен для всех
                .antMatchers("/api/auth/login").permitAll() // Доступен для всех
                .anyRequest()
                .authenticated() // Каждый запрос должен быть аутентифицирован
                .and()
                .apply(jwtConfigurer); // в дальнейшем, конфигурация пользователя будет осуществляться на основании JwtConfigurer
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
