package com.tomakehurst.springclosuretemplates.web.mvc;

import org.springframework.core.io.Resource;

public class ClosureTemplateConfigurer implements ClosureTemplateConfig {
	
	private Resource templatesLocation;

	public void setTemplatesLocation(Resource templatesLocation) {
		this.templatesLocation = templatesLocation;
	}

	@Override
	public Resource getTemplatesLocation() {
		return templatesLocation;
	}
}
