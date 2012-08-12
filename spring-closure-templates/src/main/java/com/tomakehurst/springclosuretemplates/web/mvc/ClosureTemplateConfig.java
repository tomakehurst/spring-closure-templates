package com.tomakehurst.springclosuretemplates.web.mvc;

import org.springframework.core.io.Resource;


public interface ClosureTemplateConfig {

	Resource getTemplatesLocation();
	boolean isRecursive();
	boolean isDevMode();
}
