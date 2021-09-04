package io.security.corespringsecurity.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.security.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import io.security.corespringsecurity.security.filter.AjaxLoginProcessingFilter;
import io.security.corespringsecurity.security.handler.CustomAccessDeniedHandler;
import io.security.corespringsecurity.security.provider.FormAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler customAuthenticationFailureHandler;

	@Autowired
	private FormAuthenticationDetailsSource formAuthenticationDetailsSource;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvier());
	}

	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/", "/users", "user/login/**", "/login*").permitAll()
			.antMatchers("/mypage").hasRole("USER")
			.antMatchers("/messages").hasRole("MANAGER")
			.antMatchers("/config").hasRole("ADMIN")
			.anyRequest().authenticated()
		.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_proc")
			.authenticationDetailsSource(formAuthenticationDetailsSource)
			.successHandler(customAuthenticationSuccessHandler)
			.failureHandler(customAuthenticationFailureHandler)
			.permitAll()
		.and()
			.exceptionHandling()
			.accessDeniedHandler(accessDeniedHandler())
		.and()
			.addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

		http.csrf().disable();
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler();
		accessDeniedHandler.setErrorPage("/denied");
		return accessDeniedHandler;
	}

	@Bean
	public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
		AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
		ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
		return ajaxLoginProcessingFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvier() {
		return new FormAuthenticationProvider(passwordEncoder());
	}

}
