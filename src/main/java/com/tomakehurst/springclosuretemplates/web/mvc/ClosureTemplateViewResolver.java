package com.tomakehurst.springclosuretemplates.web.mvc;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import com.google.template.soy.tofu.SoyTofu;

public class ClosureTemplateViewResolver extends AbstractTemplateViewResolver {

	private ClosureTemplateConfig closureTemplateConfig;
	private SoyTofu compiledTemplates;
	
	public ClosureTemplateViewResolver() {
		super();
		setExposeSpringMacroHelpers(false);
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
			view.setCompiledTemplates(closureTemplateConfig.getAllTemplatesFileSet().compileToJavaObj());
		}
		
		return view;
	}
	
	@Override
	protected void initApplicationContext() {
		super.initApplicationContext();
		compiledTemplates = closureTemplateConfig.getAllTemplatesFileSet().compileToJavaObj();
	}
}
