package com.tomakehurst.springclosuretemplates.web.mvc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.google.template.soy.SoyFileSet;

public class ClosureTemplateConfigurer implements ClosureTemplateConfig, InitializingBean {
	
	private Resource templatesLocation;
	private SoyFileSet soyFileSet;

	@Override
	public SoyFileSet getAllTemplatesFileSet() {
		return soyFileSet;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		File[] templateFiles;
		try {
			templateFiles = templatesLocation.getFile().listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".soy");
				}
			});
		} catch (IOException ioe) {
			throw new IllegalArgumentException(templatesLocation.toString() + " does not exist or is not a directory", ioe);
		}
		
		SoyFileSet.Builder fileSetBuilder = new SoyFileSet.Builder();
		for (File templateFile: templateFiles) {
			fileSetBuilder.add(templateFile);
		}
		
		soyFileSet = fileSetBuilder.build();
	}

	public Resource getTemplatesLocation() {
		return templatesLocation;
	}

	public void setTemplatesLocation(Resource templatesLocation) {
		this.templatesLocation = templatesLocation;
	}
}
