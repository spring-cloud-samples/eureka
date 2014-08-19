package eurekademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 *
 * @author Gunnar Hillert
 *
 */
@ComponentScan
@Configuration
@EnableAutoConfiguration
public class EurekaApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean jersey() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new ServletContainer());
		bean.addInitParameter("com.sun.jersey.config.property.packages",
				"com.netflix.discovery;com.netflix.eureka");
		bean.setUrlPatterns(Lists.newArrayList("/v2/*"));
		return bean;
	}

}
