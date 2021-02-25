package edu.edu.cs518.angelopoulos.researchbackend.config;

import edu.edu.cs518.angelopoulos.researchbackend.security.FirebaseIdTokenFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

/**
 * Security configuration for endpoint authorization.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Configuration for authorizing endpoints.
     *
     * @param http HttpSecurity object
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/public/*").permitAll()
                .mvcMatchers("/private/*").authenticated()
                .mvcMatchers("/private-admin/*").hasAuthority("admin")
                .and()
                .cors()
                .and()
                .addFilterBefore(firebaseIdTokenFilterBean(), (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);

        // Disable CSRF
        http.csrf().disable();
    }

    /**
     * Creates and returns a {@link edu.edu.cs518.angelopoulos.researchbackend.security.FirebaseIdTokenFilter} object
     *
     * @return FirebaseIdTokenFilter object
     */
    public FirebaseIdTokenFilter firebaseIdTokenFilterBean() {
        return new FirebaseIdTokenFilter();
    }
}
