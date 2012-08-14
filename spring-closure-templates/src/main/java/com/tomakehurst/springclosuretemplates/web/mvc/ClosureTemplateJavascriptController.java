package com.tomakehurst.springclosuretemplates.web.mvc;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;

@Controller
public class ClosureTemplateJavascriptController {
	
	private String cacheControl = "public, max-age=3600";
	
	@Autowired
	private ClosureTemplateConfig config;
	
	//TODO: Make properties on this settable via config
	private SoyJsSrcOptions jsSrcOptions = new SoyJsSrcOptions();
	
	private ConcurrentHashMap<String, String> cachedJsTemplates = new ConcurrentHashMap<String, String>();

	@Autowired
	public ClosureTemplateJavascriptController(ClosureTemplateConfig config) {
		this.config = config;
	}

	@RequestMapping(value="/tmpl/{templateFileName}.js", method=GET)
	public ResponseEntity<String> getJsForTemplateFile(@PathVariable String templateFileName) {
		if (!config.isDevMode() && cachedJsTemplates.containsKey(templateFileName)) {
			return prepareResponseFor(cachedJsTemplates.get(templateFileName));
		} else {
			File templateFile = getTemplateFileAndAssertExistence(templateFileName);
			String templateContent = compileTemplateAndAssertSuccess(templateFile);
			cachedJsTemplates.putIfAbsent(templateFileName, templateContent);
			ResponseEntity<String> response = prepareResponseFor(templateContent);
			return response;
		}
	}

	private ResponseEntity<String> prepareResponseFor(String templateContent) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/javascript");
		headers.add("Cache-Control", config.isDevMode() ? "no-cache" : cacheControl);
		ResponseEntity<String> response = new ResponseEntity<String>(templateContent, headers, OK);
		return response;
	}

	private String compileTemplateAndAssertSuccess(File templateFile) {
		SoyFileSet soyFileSet = buildSoyFileSetFrom(templateFile);
		
		List<String> compiledTemplates = soyFileSet.compileToJsSrc(jsSrcOptions, null);
		if (compiledTemplates.size() < 1) {
			throw notFound("No compiled templates found");
		}
		
		String templateContent = compiledTemplates.get(0);
		return templateContent;
	}

	private SoyFileSet buildSoyFileSetFrom(File templateFile) {
		SoyFileSet soyFileSet = (new SoyFileSet.Builder()).add(templateFile).build();
		return soyFileSet;
	}

	private File getTemplateFileAndAssertExistence(String templateFileName) {
		File templateFile;
		try {
			templateFile = new File(config.getTemplatesLocation().getFile(), templateFileName + ".soy");
		} catch (IOException ioe) {
			throw notFound(templateFileName);
		}
		
		if (!templateFile.exists() || !templateFile.isFile()) {
			throw notFound(templateFileName);
		}
		return templateFile;
	}

	private HttpClientErrorException notFound(String file) {
		return new HttpClientErrorException(NOT_FOUND, file);
	}
	
	public void setConfig(ClosureTemplateConfig config) {
		this.config = config;
	}
	
	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

}
