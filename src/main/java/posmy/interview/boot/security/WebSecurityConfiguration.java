package posmy.interview.boot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.services.AuthenticationService;

/**
 * @author Rashidi Zin
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private AuthenticationService authenticationService;

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // http.authorizeRequests().anyRequest().authenticated();

        // http.authorizeRequests().anyRequest().permitAll();
        // need to disable csrf else it will returned 403 forbidden error
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/books").authenticated()
                .antMatchers(HttpMethod.GET, "/users").hasAnyRole(UserRole.LIBRARIAN.getRole(), UserRole.MEMBER.getRole()) // librarian can read all users
                .antMatchers(HttpMethod.GET, "/users/**").hasAnyRole(UserRole.LIBRARIAN.getRole(), UserRole.MEMBER.getRole()) // librarian and member can get acc details
                .antMatchers(HttpMethod.POST, "/users").hasRole(UserRole.LIBRARIAN.getRole()) // librarian can create user
                .antMatchers(HttpMethod.DELETE, "/users/**")
                .hasAnyRole(UserRole.LIBRARIAN.getRole(), UserRole.MEMBER.getRole()) // librarian can delete any user while member only can delete own acc
                .antMatchers(HttpMethod.DELETE, "/users").hasRole(UserRole.LIBRARIAN.getRole()) // librarian can delete all users
                .antMatchers(HttpMethod.POST, "/books").hasRole(UserRole.LIBRARIAN.getRole()) // librarian can create book
                .antMatchers(HttpMethod.DELETE, "/books/**").hasAnyRole(UserRole.LIBRARIAN.getRole()) // librarian can delete books
                .antMatchers(HttpMethod.PUT, "/books/**").hasRole(UserRole.LIBRARIAN.getRole()) // librarian can update book
                .antMatchers(HttpMethod.PATCH, "/books/borrow/**", "/books/return/**")
                .hasRole(UserRole.MEMBER.getRole()) // member can borrow / return book
                .and()
                .cors() // enable cors
                .and()
                .formLogin().disable()// disable form login
                .headers().frameOptions().disable();      // to enable h2 web console access

        http.csrf().disable();// close CSRF protection
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

