package edu.cs518.angelopoulos.research.backend.config;

import edu.cs518.angelopoulos.research.backend.security.FirebaseIdTokenFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

/**
 * Security configuration for endpoint authorization.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Configuration for authorizing endpoints.
     *
     * @param httpSecurity HttpSecurity object
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/private/**").authenticated()
                .antMatchers("/private-admin/**").hasAuthority("admin")
                .and()
                .addFilterBefore(firebaseIdTokenFilterBean(), (Class<? extends Filter>) UsernamePasswordAuthenticationFilter.class);

        httpSecurity
                .cors()
                .and()
                .csrf().disable();
    }

    /**
     * Creates and returns a {@link FirebaseIdTokenFilter} object
     *
     * @return FirebaseIdTokenFilter object
     */
    public FirebaseIdTokenFilter firebaseIdTokenFilterBean() {
        return new FirebaseIdTokenFilter();
    }
}
