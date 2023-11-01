package com.dam.config;

import com.dam.commons.Routes;
import com.dam.modules.jwt.JwtFilter;
import com.dam.modules.user.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {


    private final UserService userService;
    private final DataSource dataSource;
    private final JwtFilter jwtFilter;

    @Autowired
    public SpringSecurityConfig(DataSource dataSource, UserService userService, JwtFilter jwtFilter) {
        this.dataSource = dataSource;
        this.userService = userService;
        this.jwtFilter = jwtFilter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                //  .cors().disable()
                .authorizeRequests()
                .antMatchers("/**",
                        Routes.POST_user_verify_email,
                        Routes.POST_user_verify_mobile,
                        Routes.POST_user_auth_email,
                        Routes.POST_user_auth_mobile,
                        Routes.POST_login,
                        Routes.POST_reset_pass_email,
                        Routes.POST_reset_pass_mobile,
                        Routes.POST_admin_login,
                        Routes.POST_forget_pass_email,
                        Routes.POST_forget_pass_mobile
                ).permitAll()
                .antMatchers("/upload/**", "/test/**").permitAll()//   "/dam/**"
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint((request, response, e) ->
                {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(new JSONObject()
                            .put("status", "fail")
                            .put("code", HttpServletResponse.SC_FORBIDDEN)
                            .put("message", "Your token has expired.")
                            .toString());
                })
                .accessDeniedHandler((request, response, e) ->
                {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write(new JSONObject()
                            .put("status", "fail")
                            .put("code", HttpServletResponse.SC_FORBIDDEN)
                            .put("message", "You do not have access to this section")
                            .toString());
                });

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
