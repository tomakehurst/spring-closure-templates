package com.tomakehurst.springclosuretemplates.web.mvc;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

public class ClosureTemplateConfigurerTest {
	
	private ClosureTemplateConfigurer configurer;
	
	@Before
	public void init() {
		configurer = new ClosureTemplateConfigurer();
	}

	@Test
	public void shouldReturnSoyFileSetWhenValidTemplateLocationSupplied() throws Exception {
		configurer.setTemplatesLocation(new ClassPathResource("test-closure-templates"));
		configurer.afterPropertiesSet();

		SoyFileSet sfs = configurer.getAllTemplatesFileSet();
		SoyTofu tofu = sfs.compileToJavaObj();
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("name", "Tom");
		String html = tofu.render("com.tomakehurst.helloName", data, null);
		assertThat(html, containsString("Hello Tom"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionWhenInvalidTemplateLocationSupplied() throws Exception {
		configurer.setTemplatesLocation(new ClassPathResource("/some/non-existent/closure-templates"));
		configurer.afterPropertiesSet();
		configurer.getAllTemplatesFileSet();
	}
}
