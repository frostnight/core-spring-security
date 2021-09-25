package io.security.corespringsecurity.service;

import java.util.List;

import io.security.corespringsecurity.domain.entity.Resources;

public interface ResourcesService {

	Resources getResources(Long id);

	List<Resources> getResources();

	void createResources(Resources Resources);

	void deleteResources(Long id);

}
