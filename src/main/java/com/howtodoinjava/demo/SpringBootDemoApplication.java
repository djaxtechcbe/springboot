package com.howtodoinjava.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.support.SpringBootServletInitializer; its available at Spring Boot 1.4.3 Version
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import com.howtodoinjava.demo.file.FileStorageProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.ApplicationContext;	

@SpringBootApplication
@EnableCaching 
@EnableConfigurationProperties(FileStorageProperties.class)
@ImportResource("classpath:applicationContext.xml") // works
//@ImportResource("classpath:applicationContext.xml")
public class SpringBootDemoApplication extends SpringBootServletInitializer {
	
	// Only main method is enough to build jar when application starts
	/*public static void main(String[] args) {
		SpringApplication.run(SpringBootDemoApplication.class, args);
	}*/
	
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringBootDemoApplication.class);
    }

    public static void main(String[] args) throws Exception {
        ApplicationContext context = SpringApplication.run(SpringBootDemoApplication.class, args);
        for (String name : context.getBeanDefinitionNames()) {
			if(name.equalsIgnoreCase("praveen")) {
				System.out.println(name);
			}			
		}
    }
}
