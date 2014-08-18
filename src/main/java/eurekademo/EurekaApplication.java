package eurekademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import ro.isdc.wro.config.jmx.WroConfiguration;
import ro.isdc.wro.extensions.processor.css.Less4jProcessor;
import ro.isdc.wro.http.WroFilter;
import ro.isdc.wro.manager.factory.ConfigurableWroManagerFactory;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.factory.WroModelFactory;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;
import ro.isdc.wro.model.resource.processor.factory.SimpleProcessorsFactory;
import ro.isdc.wro.model.resource.processor.impl.css.CssImportPreProcessor;

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
    public EurekaInstanceRegisteredListener eurekaInstanceRegisteredListener() {
        return new EurekaInstanceRegisteredListener();
    }

    @Bean
    public EurekaInstanceRenewedListener eurekaInstanceRenewedListener() {
        return new EurekaInstanceRenewedListener();
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

	@Bean
	public FilterRegistrationBean wro4j() {
		final FilterRegistrationBean bean = new FilterRegistrationBean();
		final WroFilter wroFilter = new WroFilter();
		final WroConfiguration wroConfiguration = new WroConfiguration();
		wroConfiguration.setDebug(true);

		final ConfigurableWroManagerFactory wroManagerFactory = new ConfigurableWroManagerFactory();
		final SimpleProcessorsFactory simpleProcessorsFactory = new SimpleProcessorsFactory();

		simpleProcessorsFactory.addPreProcessor(new CssImportPreProcessor());
		simpleProcessorsFactory.addPostProcessor(new Less4jProcessor());

		wroManagerFactory.setProcessorsFactory(simpleProcessorsFactory);
		wroManagerFactory.setModelFactory(new MyWroModelFactory());
		wroFilter.setWroManagerFactory(wroManagerFactory);

		bean.setFilter(wroFilter);
		bean.setUrlPatterns(Lists.newArrayList("/wro/*"));

		return bean;
	}

	public class MyWroModelFactory implements WroModelFactory {
		public WroModel create() {
			return new WroModel().addGroup(new Group("wro")
				.addResource(Resource.create("classpath:wro/main.less", ResourceType.CSS)));
		}

		public void destroy() {
			//do some clean-up if required.
		}
	}
}
