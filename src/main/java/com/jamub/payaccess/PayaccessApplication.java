package com.jamub.payaccess;

import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.StandardEnvironment;
import com.github.ulisesbocchio.jar.resources.JarResourceLoader;

@SpringBootApplication
@EnableAutoConfiguration
public class PayaccessApplication extends SpringBootServletInitializer
//		implements ApplicationContextAware
{

	private static ApplicationContext context;

	public static void main(String[] args) {
//		StandardEnvironment environment = new StandardEnvironment();
//		new SpringApplicationBuilder()
//				.sources(PayaccessApplication.class)
//				.environment(environment)
//				.resourceLoader(new JarResourceLoader(environment, "resources.extract.dir"))
//				.build()
//				.run(args);
		SpringApplication.run(PayaccessApplication.class, args);
	}

//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		context = applicationContext;
//	}
//
//	public static ApplicationContext getContext() {
//		return context;
//	}
}