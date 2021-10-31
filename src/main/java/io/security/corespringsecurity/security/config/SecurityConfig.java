package io.security.corespringsecurity.security.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import io.security.corespringsecurity.security.common.FormAuthenticationDetailsSource;
import io.security.corespringsecurity.security.factory.UrlResourcesMapFactoryBean;
import io.security.corespringsecurity.security.filter.PermitAllFilter;
import io.security.corespringsecurity.security.handler.AjaxAuthenticationFailureHandler;
import io.security.corespringsecurity.security.handler.AjaxAuthenticationSuccessHandler;
import io.security.corespringsecurity.security.handler.FormAccessDeniedHandler;
import io.security.corespringsecurity.security.metadatasource.UrlFilterInvocationSecurityMetadatsSource;
import io.security.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import io.security.corespringsecurity.security.provider.FormAuthenticationProvider;
import io.security.corespringsecurity.security.voter.IpAddressVoter;
import io.security.corespringsecurity.service.SecurityResourceService;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationSuccessHandler formAuthenticationSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler formAuthenticationFailureHandler;

	@Autowired
	private FormAuthenticationDetailsSource formWebAuthenticationDetailsSource;

	@Autowired
	private SecurityResourceService securityResourceService;

	private String[] permitAllResources = {"/", "/login", "/user/login/**"};

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
		auth.authenticationProvider(ajaxAuthenticationProvider());
	}

	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
			.antMatchers("/mypage").hasRole("USER")
			.antMatchers("/messages").hasRole("MANAGER")
			.antMatchers("/config").hasRole("ADMIN")
			.antMatchers("/**").permitAll()
			.anyRequest().authenticated()
			.and()
			.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/login_proc")
			.authenticationDetailsSource(formWebAuthenticationDetailsSource)
			.successHandler(formAuthenticationSuccessHandler)
			.failureHandler(formAuthenticationFailureHandler)
			.permitAll()
		.and()
			.exceptionHandling()
			//                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
			.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
			.accessDeniedPage("/denied")
			.accessDeniedHandler(accessDeniedHandler())
		.and()
			.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
		http.csrf().disable();

		customConfigurer(http);
	}

	private void customConfigurer(HttpSecurity http) throws Exception {
		http
			.apply(new AjaxLoginConfigurer<>())
			.successHandlerAjax(ajaxAuthenticationSuccessHandler())
			.failureHandlerAjax(ajaxAuthenticationFailureHandler())
			.loginProcessingUrl("/api/login")
			.setAuthenticationManager(authenticationManagerBean());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(){
		return new FormAuthenticationProvider(passwordEncoder());
	}

	@Bean
	public AuthenticationProvider ajaxAuthenticationProvider(){
		return new AjaxAuthenticationProvider(passwordEncoder());
	}

	@Bean
	public AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler(){
		return new AjaxAuthenticationSuccessHandler();
	}

	@Bean
	public AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler(){
		return new AjaxAuthenticationFailureHandler();
	}

	public AccessDeniedHandler accessDeniedHandler() {
		FormAccessDeniedHandler commonAccessDeniedHandler = new FormAccessDeniedHandler();
		commonAccessDeniedHandler.setErrorPage("/denied");
		return commonAccessDeniedHandler;
	}

	@Bean
	public PermitAllFilter customFilterSecurityInterceptor() throws Exception {

		PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResources);
		permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
		permitAllFilter.setAccessDecisionManager(affirmativeBased());
		permitAllFilter.setAuthenticationManager(authenticationManagerBean());
		return permitAllFilter;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() throws Exception {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(customFilterSecurityInterceptor());
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}

	private AccessDecisionManager affirmativeBased() {
		AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDicisionVoters());
		return affirmativeBased;
	}

	private List<AccessDecisionVoter<?>> getAccessDicisionVoters() {

		List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
		accessDecisionVoters.add(new IpAddressVoter(securityResourceService));
		accessDecisionVoters.add(roleVoter());

		return accessDecisionVoters;
	}

	@Bean
	public AccessDecisionVoter<? extends Object> roleVoter() {
		RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
		return roleHierarchyVoter;
	}

	@Bean
	public RoleHierarchyImpl roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		return roleHierarchy;
	}

	@Bean
	public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
		return new UrlFilterInvocationSecurityMetadatsSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
	}

	private UrlResourcesMapFactoryBean urlResourcesMapFactoryBean() {

		UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
		urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
		return urlResourcesMapFactoryBean;
	}

}
