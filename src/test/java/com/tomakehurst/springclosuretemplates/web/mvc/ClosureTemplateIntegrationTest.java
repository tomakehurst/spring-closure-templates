package com.tomakehurst.springclosuretemplates.web.mvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.servlet.View;

import com.google.template.soy.tofu.SoyTofuException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext-test.xml"},loader=WebContextLoader.class)
public class ClosureTemplateIntegrationTest {

	@Autowired
	private ClosureTemplateViewResolver closureTemplateViewResolver;
	
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
		closureTemplateViewResolver.setCache(false);

		View view = closureTemplateViewResolver.resolveViewName("com.tomakehurst.helloName", Locale.UK);
		view.render(model, request, response);
		
		String content = response.getContentAsString();
		assertThat(content, containsString("Hello Tom"));
	}
	
	@Test
	public void shouldBuildWorkingViewWhenConfiguredCorrectlyWithTemplateCaching() throws Exception {
		closureTemplateViewResolver.setCache(true);
		
		View view = closureTemplateViewResolver.resolveViewName("com.tomakehurst.helloName", Locale.UK);
		view.render(model, request, response);
		
		String content = response.getContentAsString();
		assertThat(content, containsString("Hello Tom"));
	}
	
	@Test
	public void shouldReloadTemplateOnEachInvocationIfNoCaching() throws Exception {
		
	}
	
	@Test(expected=SoyTofuException.class)
	public void shouldThrowExceptionIfTemplateDoesntExist() throws Exception {
		View view = closureTemplateViewResolver.resolveViewName("com.tomakehurst.doesntexist", Locale.UK);
		view.render(model, request, response);
	}
}
