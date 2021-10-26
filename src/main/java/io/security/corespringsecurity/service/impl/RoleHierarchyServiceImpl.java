package io.security.corespringsecurity.service.impl;

import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.security.corespringsecurity.domain.entity.RoleHierarchy;
import io.security.corespringsecurity.repository.RoleHierarchyRepository;
import io.security.corespringsecurity.service.RoleHierarchyService;

@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

	@Autowired
	private RoleHierarchyRepository roleHierarchyRepository;

	@Transactional
	@Override
	public String findAllHierarchy() {

		List<RoleHierarchy> rolesHierarchy = roleHierarchyRepository.findAll();

		Iterator<RoleHierarchy> itr = rolesHierarchy.iterator();
		StringBuilder concatedRoles = new StringBuilder();

		while(itr.hasNext()){
			RoleHierarchy roleHierarchy = itr.next();
			if (roleHierarchy.getParentName() != null){
				concatedRoles.append(roleHierarchy.getParentName().getChildName());
				concatedRoles.append(" > ");
				concatedRoles.append(roleHierarchy.getChildName());
				concatedRoles.append("\n");
			}
		}

		return concatedRoles.toString();
	}
}
