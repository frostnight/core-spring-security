package io.security.corespringsecurity.security.config;

import java.util.Arrays;
import java.util.List;

import io.security.corespringsecurity.security.aop.CustomMethodSecurityInterceptor;
import io.security.corespringsecurity.security.enums.SecurtiyMethodType;
import io.security.corespringsecurity.security.factory.MethodResourcesMapFactoryBean;
import io.security.corespringsecurity.security.factory.UrlResourcesMapFactoryBean;
import io.security.corespringsecurity.security.filter.PermitAllFilter;
import io.security.corespringsecurity.security.metadatasource.UrlSecurityMetadataSource;
import io.security.corespringsecurity.security.processor.ProtectPointcutPostProcessor;
import io.security.corespringsecurity.security.voter.IpAddressVoter;
import io.security.corespringsecurity.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.intercept.RunAsManager;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	private String[] permitAllPattern = {"/", "/home", "/users", "/login"};

	@Autowired
	private SecurityResourceService securityResourceService;

	protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
		return mapBasedMethodSecurityMetadataSource();
	}

	@Bean
	public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
		return new MapBasedMethodSecurityMetadataSource(methodResourcesMapFactoryBean().getObject());
	}

	@Bean
	public MethodResourcesMapFactoryBean methodResourcesMapFactoryBean(){
		MethodResourcesMapFactoryBean methodResourcesMapFactoryBean = new MethodResourcesMapFactoryBean();
		methodResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
		methodResourcesMapFactoryBean.setResourceType(SecurtiyMethodType.METHOD.getValue());
		return methodResourcesMapFactoryBean;
	}

	@Bean
	public ProtectPointcutPostProcessor protectPointcutPostProcessor() {

		ProtectPointcutPostProcessor protectPointcutPostProcessor = new ProtectPointcutPostProcessor(mapBasedMethodSecurityMetadataSource());
		protectPointcutPostProcessor.setPointcutMap(pointcutResourcesMapFactoryBean().getObject());

		return protectPointcutPostProcessor;
	}

	@Bean
	public MethodResourcesMapFactoryBean pointcutResourcesMapFactoryBean(){

		MethodResourcesMapFactoryBean pointcutResourcesMapFactoryBean = new MethodResourcesMapFactoryBean();
		pointcutResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
		pointcutResourcesMapFactoryBean.setResourceType(SecurtiyMethodType.POINTCUT.getValue());
		return pointcutResourcesMapFactoryBean;
	}

	@Bean
	public PermitAllFilter permitAllFilter() {
		PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllPattern);
		permitAllFilter.setAccessDecisionManager(affirmativeBased());
		permitAllFilter.setSecurityMetadataSource(urlSecurityMetadataSource());
		return permitAllFilter;
	}


	@Bean
	public CustomMethodSecurityInterceptor customMethodSecurityInterceptor(MapBasedMethodSecurityMetadataSource methodSecurityMetadataSource) {
		CustomMethodSecurityInterceptor customMethodSecurityInterceptor =  new CustomMethodSecurityInterceptor();
		customMethodSecurityInterceptor.setAccessDecisionManager(accessDecisionManager());
		customMethodSecurityInterceptor.setAfterInvocationManager(afterInvocationManager());
		customMethodSecurityInterceptor.setSecurityMetadataSource(methodSecurityMetadataSource);
		RunAsManager runAsManager = runAsManager();
		if (runAsManager != null) {
			customMethodSecurityInterceptor.setRunAsManager(runAsManager);
		}

		return customMethodSecurityInterceptor;
	}

	@Bean
	public UrlSecurityMetadataSource urlSecurityMetadataSource() {
		return new UrlSecurityMetadataSource(urlResourcesMapFactoryBean().getObject(), securityResourceService);
	}

	@Bean
	public UrlResourcesMapFactoryBean urlResourcesMapFactoryBean(){
		UrlResourcesMapFactoryBean urlResourcesMapFactoryBean = new UrlResourcesMapFactoryBean();
		urlResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
		return urlResourcesMapFactoryBean;
	}

	@Bean
	public AccessDecisionManager affirmativeBased() {
		AffirmativeBased accessDecisionManager = new AffirmativeBased(getAccessDecisionVoters());
		return accessDecisionManager;
	}

	private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {

		IpAddressVoter ipAddressVoter = new IpAddressVoter(securityResourceService);
		List<AccessDecisionVoter<? extends Object>> accessDecisionVoterList = Arrays.asList(/*ipAddressVoter,*/ roleVoter());
		return accessDecisionVoterList;
	}

	@Bean
	public RoleHierarchyVoter roleVoter() {
		RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
		roleHierarchyVoter.setRolePrefix("ROLE_");
		return roleHierarchyVoter;
	}

	@Bean
	public RoleHierarchyImpl roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		return roleHierarchy;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() throws Exception {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(permitAllFilter());
		filterRegistrationBean.setEnabled(false);
		return filterRegistrationBean;
	}
}
