package com.example.es_springboot_security_basis.config;

import com.example.es_springboot_security_basis.model.Permission;
import com.example.es_springboot_security_basis.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Необходим для работы @PreAuthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // Аутентификация   - (Ошибка 401) Разграничение на друзей и врагов
    // Авторизация      - (Ошибка 403) К каким страницам и каким ресурсам человек имеет доступ

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Будет использоваться своя конфигурация
        http
                .csrf().disable() // Отключение csrf
                .authorizeRequests()
                .antMatchers("/")
                .permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(Permission.DEVELOPERS_READ.getPermission()) // Работа с permission
                .antMatchers(HttpMethod.POST, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission()) // Работа с permission
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAuthority(Permission.DEVELOPERS_WRITE.getPermission()) // Работа с permission
                .anyRequest()
                .authenticated() // Каждый запрос должен быть аутентифицирован
                .and()
                .httpBasic(); // Использование Base64 для шифрования
    }

    // Создание пользователей и паролей в памяти
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(

                User.builder()
                        .username("admin")
                        .password(passwordEncoder().encode("admin"))
                        .authorities(Role.ADMIN.getAuthorities()) // Добавление разрешений SimpleGrantedAuthority
                        .build(),

                User.builder()
                        .username("user")
                        .password(passwordEncoder().encode("user"))
                        .authorities(Role.USER.getAuthorities()) // Добавление разрешений SimpleGrantedAuthority
                        .build()
        );
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
