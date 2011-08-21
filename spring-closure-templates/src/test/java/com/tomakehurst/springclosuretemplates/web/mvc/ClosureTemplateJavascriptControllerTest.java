package com.tomakehurst.springclosuretemplates.web.mvc;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

public class ClosureTemplateJavascriptControllerTest {

	private ClosureTemplateConfigurer config;
	private ClosureTemplateJavascriptController controller;
	private HttpHeaders headers;
	private String js;
	
	@Before
	public void init() {
		config = new ClosureTemplateConfigurer();
		config.setTemplatesLocation(new FileSystemResource("src/test/resources/test-closure-templates"));
		controller = new ClosureTemplateJavascriptController(config);
	}
	
	@Test
	public void shouldCompileAndReturnComiledTemplateAsJavascript() {
		compileExampleOne();
		assertThat(js, containsString("Hello"));
	}

	@Test
	public void shouldSetContentTypeToTextJavascript() {
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Content-Type", "text/javascript"));
	}
	
	@Test
	public void shouldSetCacheControlAccordingToProperty() {
		controller.setCacheControl("public, max-age=900");
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Cache-Control", "public, max-age=900"));
	}
	
	@Test
	public void shouldSetCacheControlForOneHourWhenNoCacheControlSet() {
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Cache-Control", "public, max-age=3600"));
	}
	
	@Test
	public void shouldSetCacheControlToNoCacheWhenInDevMode() {
		config.setDevMode(true);
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Cache-Control", "no-cache"));
	}
	
	@Test
	public void shouldThrowNotFoundWhenNonExistentTemplateFileNameUsed() {
		boolean exceptionThrown = false;
		
		try {
			controller.getJsForTemplateFile("example-nothing");
		} catch (HttpClientErrorException e) {
			exceptionThrown = true;
			assertThat(e.getStatusCode(), is(NOT_FOUND));
		}
		
		assertTrue(exceptionThrown);
	}
	
	private void compileExampleOne() {
		ResponseEntity<String> response = controller.getJsForTemplateFile("example-one");
		js = response.getBody();
		headers = response.getHeaders();
	}
}
