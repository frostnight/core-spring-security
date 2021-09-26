package io.security.corespringsecurity.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.security.corespringsecurity.domain.entity.Role;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.service.RoleService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Override
	@Transactional
	public Role getRole(Long id) {
		return roleRepository.findById(id).orElse(new Role());
	}

	@Override
	@Transactional
	public List<Role> getRoles() {
		return roleRepository.findAll();
	}

	@Override
	@Transactional
	public void createRole(Role role) {
		roleRepository.save(role);
	}

	@Override
	@Transactional
	public void deleteRole(Long id) {
		roleRepository.deleteById(id);
	}
}
