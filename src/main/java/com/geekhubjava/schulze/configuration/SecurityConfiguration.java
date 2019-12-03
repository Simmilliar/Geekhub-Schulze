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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private UserAuthDetailsService userAuthDetailsService;
    private RedirectAccessDeniedHandler redirectAccessDeniedHandler;

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
        web.ignoring().antMatchers("/js/**", "/css/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/registration", "/login*").anonymous()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling()
                .accessDeniedHandler(redirectAccessDeniedHandler)
                .and()
            .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login")
                .defaultSuccessUrl("/")
                .and()
            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID");
    }
}
