package com.tomakehurst.springclosuretemplates.web.mvc;

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

	private ClosureTemplateJavascriptController controller;
	private HttpHeaders headers;
	private String js;
	
	@Before
	public void init() {
		controller = new ClosureTemplateJavascriptController();
		ClosureTemplateConfigurer config = new ClosureTemplateConfigurer();
		config.setTemplatesLocation(new FileSystemResource("src/test/resources/test-closure-templates"));
		controller.setConfig(config);
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
	public void shouldSetCachePolicyAccordingToProperty() {
		controller.setCacheControl("public, max-age=900");
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Cache-Control", "public, max-age=900"));
	}
	
	@Test
	public void shouldCacheForOneHourWhenNoCacheControlSet() {
		compileExampleOne();
		assertThat(headers.toSingleValueMap(), hasEntry("Cache-Control", "public, max-age=3600"));
	}
	
	@Test
	public void shouldThrowNotFoundWhenNonExistentTemplateFileNameUsed() {
		try {
			controller.compileJsForTemplateFile("example-nothing");
		} catch (HttpClientErrorException e) {
			assertThat(e.getStatusCode(), is(NOT_FOUND));
		}
	}
	
	private void compileExampleOne() {
		ResponseEntity<String> response = controller.compileJsForTemplateFile("example-one");
		js = response.getBody();
		headers = response.getHeaders();
	}
}
