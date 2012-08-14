package com.tomakehurst.springclosuretemplates.web.mvc;

import com.google.template.soy.tofu.SoyTofuException;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"},loader=WebContextLoader.class)
public class ClosureTemplateIntegrationTest {

    private static final Resource TEMPLATES_ROOT = new ClassPathResource("/test-closure-templates");

	@Autowired
	private ClosureTemplateConfigurer config;

	@Autowired
	private ClosureTemplateJavascriptController jsController;

	@Autowired
	@Qualifier("closureTemplateViewResolver")
	private ClosureTemplateViewResolver cachingClosureTemplateViewResolver;

	@Autowired
	@Qualifier("noCacheClosureTemplateViewResolver")
	private ClosureTemplateViewResolver noCacheClosureTemplateViewResolver;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private Map<String, Object> model;

	@Before
	public void init() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		model = new HashMap<String, Object>();
		model.put("name", "Tom");
	}

	@Test
	public void shouldBuildWorkingViewWhenConfiguredCorrectlyWithNoTemplateCaching() throws Exception {
		View view = noCacheClosureTemplateViewResolver.resolveViewName("com.tomakehurst.helloName", Locale.UK);
		String content = render(view);

		assertThat(content, containsString("Hello Tom"));
	}

	@Test
	public void shouldBuildWorkingViewWhenConfiguredCorrectlyWithTemplateCaching() throws Exception {
		View view = cachingClosureTemplateViewResolver.resolveViewName("com.tomakehurst.helloName", Locale.UK);

		String content = render(view);
		assertThat(content, containsString("Hello Tom"));
	}

	@Test
	public void shouldBuildWorkingSubFolderViewWhenConfiguredAsRecursive() throws Exception {
		View view = cachingClosureTemplateViewResolver.resolveViewName("com.tomakehurst.subdir.helloName2", Locale.UK);

		String content = render(view);
		assertThat(content, containsString("Hello Tom"));
	}

	@Test(expected=SoyTofuException.class)
	public void shouldThrowExceptionIfTemplateDoesntExist() throws Exception {
		View view = cachingClosureTemplateViewResolver.resolveViewName("com.tomakehurst.doesntexist", Locale.UK);
		view.render(model, request, response);
	}

	@Test
	public void shouldReloadTemplateOnEachInvocationIfNoCaching() throws Exception {
		String initialTemplate = getTemplateContent("example-one.soy").replace("helloName", "myNameIs");
		writeNewTemplateFile("tmp-example.soy", initialTemplate);

		View view = noCacheClosureTemplateViewResolver.resolveViewName("com.tomakehurst.myNameIs", Locale.UK);
		model.put("name", "Jimmy");
		String content = render(view);
		assertThat(content, is("Hello Jimmy!"));

		String newTemplate = initialTemplate.replace("Hello {$name}", "My name is {$name}");
		writeNewTemplateFile("tmp-example.soy", newTemplate);
		view = noCacheClosureTemplateViewResolver.resolveViewName("com.tomakehurst.myNameIs", Locale.UK);
		content = render(view);
		assertThat(content, is("My name is Jimmy!"));
	}

	@Test(expected=SoyTofuException.class)
	public void shouldThrowExceptionIfNewTemplateLoadAttemptedWhenCaching() throws Exception {
		String initialTemplate = getTemplateContent("example-one.soy").replace("helloName", "myNameIs");
		writeNewTemplateFile("tmp-example-2.soy", initialTemplate);

		View view = cachingClosureTemplateViewResolver.resolveViewName("com.tomakehurst.myNameIs", Locale.UK);
		model.put("name", "Jimmy");
		render(view);
	}

	@Test
	public void shouldNotRecompileJsOnEachInvocationIfNotInDevMode() throws Exception {
		config.setDevMode(false);
		String initialTemplate = getTemplateContent("example-one.soy").replace("helloName", "myNameIs");
		writeNewTemplateFile("tmp-js-example.soy", initialTemplate);
		String initialJs = jsController.getJsForTemplateFile("tmp-js-example").getBody();

		String newTemplate = initialTemplate.replace("Hello {$name}", "My name is {$name}");
		writeNewTemplateFile("tmp-js-example.soy", newTemplate);
		String finalJs = jsController.getJsForTemplateFile("tmp-js-example").getBody();

		assertThat(initialJs, is(finalJs));
	}

	@Test
	public void shouldRecompileJsOnEachInvocationIfInDevMode() throws Exception {
		config.setDevMode(true);
		String initialTemplate = getTemplateContent("example-one.soy").replace("helloName", "myNameIs");
		writeNewTemplateFile("tmp-js-example.soy", initialTemplate);
		String initialJs = jsController.getJsForTemplateFile("tmp-js-example").getBody();

		String newTemplate = initialTemplate.replace("Hello {$name}", "My name is {$name}");
		writeNewTemplateFile("tmp-js-example.soy", newTemplate);
		String finalJs = jsController.getJsForTemplateFile("tmp-js-example").getBody();

		assertThat(initialJs, not(finalJs));
	}

	private String render(View view) throws Exception {
		response = new MockHttpServletResponse();
		view.render(model, request, response);
		return response.getContentAsString();
	}

	private String getTemplateContent(String templateFileName) throws IOException {
		File templateFile = getTemplateFile(templateFileName);
		return FileUtils.readFileToString(templateFile);
	}

	private void writeNewTemplateFile(String templateFileName, String content) throws IOException {
		FileUtils.write(getTmpTemplateFile(templateFileName), content);
	}

	private File getTemplateFile(String templateFileName) throws IOException {
        File tmpTemplateDir = TEMPLATES_ROOT.getFile();

		if (!tmpTemplateDir.exists()) {
			tmpTemplateDir.mkdirs();
		}
		
		File templateFile = new File(tmpTemplateDir, templateFileName);
		return templateFile;
	}
	
	private File getTmpTemplateFile(String templateFileName) throws IOException {
		File file = getTemplateFile(templateFileName);
		file.deleteOnExit();
		return file;
	}
	
	
	
}
