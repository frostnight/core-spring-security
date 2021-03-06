package io.security.corespringsecurity.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;

import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.repository.AccessIpRepository;
import io.security.corespringsecurity.repository.ResourcesRepository;

@Service
public class SecurityResourceService {

	private ResourcesRepository resourcesRepository;
	private RoleHierarchyImpl roleHierarchy;
	private AccessIpRepository accessIpRepository;
	private RoleHierarchyService roleHierarchyService;

	public SecurityResourceService(ResourcesRepository resourcesRepository, RoleHierarchyImpl roleHierarchy, RoleHierarchyService roleHierarchyService, AccessIpRepository accessIpRepository) {
		this.resourcesRepository = resourcesRepository;
		this.roleHierarchy = roleHierarchy;
		this.roleHierarchyService = roleHierarchyService;
		this.accessIpRepository = accessIpRepository;
	}
	public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList(){
		LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
		List<Resources> resourcesList = resourcesRepository.findAllResources();
		resourcesList.forEach(re -> {
			List<ConfigAttribute> configAttributeList = new ArrayList<>();
			re.getRoleSet().forEach(role -> {
				configAttributeList.add(new SecurityConfig(role.getRoleName()));
				result.put(new AntPathRequestMatcher(re.getResourceName()), configAttributeList);
			});

		});
		return result;
	}

	public LinkedHashMap<String, List<ConfigAttribute>> getMethodResourceList(){

		LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();
		List<Resources> resourcesList = resourcesRepository.findAllMethodResources();
		getResourceMap(result, resourcesList);
		return result;
	}

	public LinkedHashMap<String, List<ConfigAttribute>> getPointcutResourceList() {
		LinkedHashMap<String, List<ConfigAttribute>> result = new LinkedHashMap<>();
		List<Resources> resourcesList = resourcesRepository.findAllPointcutResources();
		getResourceMap(result, resourcesList);
		return result;
	}

	private void getResourceMap(LinkedHashMap<String, List<ConfigAttribute>> result, List<Resources> resourcesList) {
		resourcesList.forEach(re ->
			{
				List<ConfigAttribute> configAttributeList = new ArrayList<>();
				re.getRoleSet().forEach(ro -> {
					configAttributeList.add(new SecurityConfig(ro.getRoleName()));
					result.put(re.getResourceName(), configAttributeList);
				});
			}
		);
	}

	public void setRoleHierarchy() {
		String allHierarchy = roleHierarchyService.findAllHierarchy();
		roleHierarchy.setHierarchy(allHierarchy);
	}


	public List<String> getAccessIpList() {
		List<String> accessIpList = accessIpRepository.findAll().stream().map(accessIp -> accessIp.getIpAddress()).collect(Collectors.toList());
		return accessIpList;
	}
}
