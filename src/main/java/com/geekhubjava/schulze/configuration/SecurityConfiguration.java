package com.geekhubjava.schulze.configuration;

import com.geekhubjava.schulze.service.UserAuthDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserAuthDetailsService userAuthDetailsService;
    private final RedirectAccessDeniedHandler redirectAccessDeniedHandler;

    @Autowired
    public SecurityConfiguration(UserAuthDetailsService userAuthDetailsService,
                                 RedirectAccessDeniedHandler redirectAccessDeniedHandler) {
        this.userAuthDetailsService = userAuthDetailsService;
        this.redirectAccessDeniedHandler = redirectAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userAuthDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/*.js", "/*.css");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/registration", "/api/registration", "/login", "/api/login").anonymous()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling()
                .accessDeniedHandler(redirectAccessDeniedHandler)
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler())
                .failureHandler(failureHandler())
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID");
    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            response.getWriter().append("{ \"status\": \"OK\" }");
            response.setStatus(200);
        };
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            exception.printStackTrace();
            response.getWriter()
                    .append("{ \"status\": \"UNAUTHORIZED\", \"message\": \"")
                    .append(exception.getLocalizedMessage())
                    .append("\" }");
            response.setStatus(401);
        };
    }
}
