package io.security.corespringsecurity.security.config;

import io.security.corespringsecurity.security.enums.SecurtiyMethodType;
import io.security.corespringsecurity.security.factory.MethodResourcesMapFactoryBean;
import io.security.corespringsecurity.security.processor.ProtectPointcutPostProcessor;
import io.security.corespringsecurity.service.SecurityResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

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

}
