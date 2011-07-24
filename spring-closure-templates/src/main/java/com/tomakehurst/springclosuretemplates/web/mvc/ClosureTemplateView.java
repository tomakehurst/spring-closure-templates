package com.tomakehurst.springclosuretemplates.web.mvc;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import com.google.template.soy.tofu.SoyTofu;

public class ClosureTemplateView extends AbstractTemplateView {
	
	private SoyTofu compiledTemplates;
	private String templateName;

	@Override
	protected void renderMergedTemplateModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String rendition = compiledTemplates.render(templateName, model, null);
		response.getWriter().write(rendition);
	}
	
	public void setCompiledTemplates(SoyTofu compiledTemplates) {
		this.compiledTemplates = compiledTemplates;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
