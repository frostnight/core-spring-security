package io.security.corespringsecurity.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.security.corespringsecurity.domain.entity.Resources;
import io.security.corespringsecurity.repository.ResourcesRepository;
import io.security.corespringsecurity.service.ResourcesService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResourcesServiceImpl implements ResourcesService {

	private ResourcesRepository ResourcesRepository;

	@Override
	@Transactional
	public Resources getResources(Long id) {
		return ResourcesRepository.findById(id).orElse(new Resources());
	}

	@Override
	@Transactional
	public List<Resources> getResources() {
		return ResourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
	}

	@Override
	@Transactional
	public void createResources(Resources resources) {
		ResourcesRepository.save(resources);
	}

	@Override
	@Transactional
	public void deleteResources(Long id) {
		ResourcesRepository.deleteById(id);
	}
}
