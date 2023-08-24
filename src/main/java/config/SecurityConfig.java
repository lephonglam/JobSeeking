package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    @Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
		auth.authenticationProvider(authProvider);
	}
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.addFilterBefore(new EncodingFilter(), ChannelProcessingFilter.class);
		http
        .authorizeRequests()
        .antMatchers("/register-user/**").permitAll()
        .antMatchers("/detail/**").permitAll()
        .antMatchers("/register-employer/**").permitAll()
		.antMatchers("/user/**", "/search/**", "/job/**").hasAnyAuthority("USER", "ADMIN", "EMPLOYER")
		.antMatchers("/admin/**").hasAuthority("ADMIN")
		.antMatchers("/employer/**", "/recruitment/**").hasAuthority("EMPLOYER")
		.anyRequest().permitAll()
		.and()
		.formLogin()
	      .loginPage("/login")
	      .loginProcessingUrl("/doLogin")
	      .defaultSuccessUrl("/loginSuccess")
	      .failureUrl("/login?error=true")
	    .and()
	    .logout()
	    .logoutRequestMatcher(new AntPathRequestMatcher("/doLogout"))
//	      .logoutUrl("/doLogout")
	      .logoutSuccessUrl("/")
	      .deleteCookies("JSESSIONID")
	      .and()
	      .csrf();
		}
}