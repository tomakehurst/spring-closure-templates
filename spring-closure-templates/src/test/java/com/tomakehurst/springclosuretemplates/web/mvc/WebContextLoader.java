package com.tomakehurst.springclosuretemplates.web.mvc;

import static org.springframework.web.context.WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextLoader;
import org.springframework.web.context.support.GenericWebApplicationContext;

public class WebContextLoader implements ContextLoader {

	@Override
	public String[] processLocations(Class<?> clazz, String... locations) {
		return locations;
	}

	@Override
	public ApplicationContext loadContext(String... locations) throws Exception {
		MockServletContext servletContext = new MockServletContext();
		
		GenericWebApplicationContext context = new GenericWebApplicationContext();
		context.setServletContext(servletContext);
		
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);
		reader.loadBeanDefinitions(locations);
		AnnotationConfigUtils.registerAnnotationConfigProcessors(context); 
		context.registerShutdownHook();
		context.refresh();
		
		servletContext.setAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		
		return context;
	}

}
