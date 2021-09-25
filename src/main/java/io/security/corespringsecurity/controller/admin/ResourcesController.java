package io.security.corespringsecurity.controller.admin;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.repository.RoleRepository;
import io.security.corespringsecurity.service.ResourcesService;
import io.security.corespringsecurity.service.RoleService;

@Controller
public class ResourcesController {

	@Autowired
	private ResourcesService resourceService;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private RoleService roleService;

	@GetMapping(value="/admin/resources")
	public String getResources(Model model) throws Exception {

		List<Resources> resources = resourceService.getResources();
		model.addAttribute("resources", resources);

		return "admin/resource/list";
	}


}
