package com.jamub.payaccess.api.config;


import com.jamub.payaccess.api.providers.PayAccessAuthenticationProvider;
import com.jamub.payaccess.api.providers.TokenProvider;
import com.jamub.payaccess.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PayAccessAuthenticationProvider authProvider;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserService userService;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);
        return authenticationManagerBuilder.build();
    }

////    @Autowired
//    private final PayAccessAuthenticationProvider authProvider;
//
//    @Autowired
//    public SecurityConfig(PayAccessAuthenticationProvider authenticationProvider) {
//        this.authProvider = authenticationProvider;
//    }
//
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(authProvider);
//    }
//
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .apply(new JwtConfigurer(tokenProvider, userService))
                .and()
                .cors().and()
//                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
//                .accessDeniedHandler(new PayAccessExceptionHandler())
//                .and()

//                .formLogin()
//                .passwordParameter("password")
////                .loginPage("/login")
//                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/api-docs").permitAll()
                .antMatchers("/configuration/ui").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/configuration/security").permitAll()
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/swagger-ui/*").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/v2/**").permitAll()
                .antMatchers("/api/v1/invoice/get-invoice-details-for-guest/**").permitAll()
                .antMatchers("/api/v1/user/activate-account").permitAll()
                .antMatchers("/api/v1/invoice/mark-invoice-paid/**").permitAll()
                .antMatchers("/api/v1/user/update-forgot-password-admin").permitAll()
                .antMatchers("/api/v1/auth/authenticate", "/api/v1/user/forgot-password",
                        "/api/v1/transactions/authorize-card-payment-otp", "/api/v1/transactions/debit-card",
                        "/api/v1/transactions/test-validate-transaction", "/api/v1/user/set-password",
                        "/api/v1/user/update-forgot-password", "/api/v1/user/new-signup").permitAll() //"/api/v1/acquirers/create-user",
                .anyRequest().authenticated()
                .and()
                .authenticationProvider(authProvider)
//                .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint).and()
                ;
//                .authenticationEntryPoint(authenticationEntryPoint())

//        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

//        http.authenticationProvider(authProvider)
//                .authorizeRequests((authorize) -> {
//            System.out.println("Authzsss");
//            authorize.antMatchers("/api/v1/acquirers/authenticate", "/api/v1/user/forgot-password",
//                            "/api/v1/user/update-forgot-password", "/api/v1/user/set-password").permitAll()
//                    .anyRequest().authenticated();
//        });
    }
//
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        return new PayAccessAuthenticationProvider();
//    }
//
//    @Bean
//    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder =
//                http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.authenticationProvider(authProvider);
//        return authenticationManagerBuilder.build();
//    }
//
//    @Bean
//    public BCryptPasswordEncoder encoder(){
//        return new BCryptPasswordEncoder();
//    }

}
