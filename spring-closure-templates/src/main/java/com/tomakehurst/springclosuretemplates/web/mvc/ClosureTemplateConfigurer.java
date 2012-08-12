package com.tomakehurst.springclosuretemplates.web.mvc;

import org.springframework.core.io.Resource;

public class ClosureTemplateConfigurer implements ClosureTemplateConfig {
	
	private Resource templatesLocation;
	private boolean recursive = true;
	private boolean devMode = false;

	public void setTemplatesLocation(Resource templatesLocation) {
		this.templatesLocation = templatesLocation;
	}

	@Override
	public Resource getTemplatesLocation() {
		return templatesLocation;
	}

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isDevMode() {
		return devMode;
	}

	public void setDevMode(boolean devMode) {
		this.devMode = devMode;
    }
}
