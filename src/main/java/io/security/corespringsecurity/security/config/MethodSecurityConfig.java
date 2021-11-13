package io.security.corespringsecurity.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.method.MapBasedMethodSecurityMetadataSource;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import io.security.corespringsecurity.security.enums.SecurtiyMethodType;
import io.security.corespringsecurity.security.factory.methodResourcesMapFactoryBean;
import io.security.corespringsecurity.service.SecurityResourceService;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
	private SecurityResourceService securityResourceService;

	@Override
	protected MethodSecurityMetadataSource customMethodSecurityMetadataSource() {
		return mapBasedMethodSecurityMetadataSource();
	}

	@Bean
	public MapBasedMethodSecurityMetadataSource mapBasedMethodSecurityMetadataSource() {
		return new MapBasedMethodSecurityMetadataSource(methodResourcesMapFactoryBean().getObject());
	}

	@Bean
	public methodResourcesMapFactoryBean  methodResourcesMapFactoryBean(){
		methodResourcesMapFactoryBean  methodResourcesMapFactoryBean  = new methodResourcesMapFactoryBean ();
		methodResourcesMapFactoryBean.setSecurityResourceService(securityResourceService);
		methodResourcesMapFactoryBean.setResourceType(SecurtiyMethodType.METHOD.getValue());
		return methodResourcesMapFactoryBean;
	}
}
