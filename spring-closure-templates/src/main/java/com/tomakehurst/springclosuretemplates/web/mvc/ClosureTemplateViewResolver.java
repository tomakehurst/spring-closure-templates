package com.tomakehurst.springclosuretemplates.web.mvc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

public class ClosureTemplateViewResolver extends AbstractTemplateViewResolver {

	private ClosureTemplateConfig closureTemplateConfig;
	private SoyTofu compiledTemplates;
	
	public ClosureTemplateViewResolver() {
		super();
		setExposeSpringMacroHelpers(false);
	}
	
	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
		
		if (isCache()) {
			compiledTemplates = compileTemplates();
		}
	}
	
	private SoyTofu compileTemplates() {
		File[] templateFiles;
		Resource templatesLocation = closureTemplateConfig.getTemplatesLocation();
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
		
		SoyFileSet soyFileSet = fileSetBuilder.build();
		return soyFileSet.compileToJavaObj();
	}

	public void setClosureTemplateConfig(ClosureTemplateConfig closureTemplateConfig) {
		this.closureTemplateConfig = closureTemplateConfig;
	}

	@Override
	protected Class<?> getViewClass() {
		return ClosureTemplateView.class;
	}
	
	@Override
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		ClosureTemplateView view = (ClosureTemplateView) super.buildView(viewName);
		view.setTemplateName(viewName);
		
		if (isCache()) {
			view.setCompiledTemplates(compiledTemplates);
		} else {
			view.setCompiledTemplates(compileTemplates());
		}
		
		return view;
	}
	
	
}
