package demo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.cloudfoundry.community.servicebroker.config.BrokerApiVersionConfig;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.platform.netflix.eureka.EurekaRegistryAvailableEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider;
import com.netflix.eureka.PeerAwareInstanceRegistry;
import com.netflix.eureka.lease.LeaseManager;
import com.sun.jersey.spi.container.servlet.ServletContainer;

@Configuration
@ComponentScan(basePackages = {"demo", "org.cloudfoundry.community.servicebroker"}, excludeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = BrokerApiVersionConfig.class) })
@EnableAutoConfiguration
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Component
	protected static class Initializer implements
			ApplicationListener<EurekaRegistryAvailableEvent> {

		@Autowired
		private ApplicationContext applicationContext;

		@Autowired
		private Catalog catalog;

		@Autowired
		private InstanceInfo instance;

		@Override
		public void onApplicationEvent(EurekaRegistryAvailableEvent event) {
			ProxyFactory factory = new ProxyFactory(
					PeerAwareInstanceRegistry.getInstance());
			factory.addAdvice(new PiggybackMethodInterceptor(new CatalogLeaseManager(
					catalog, instance), LeaseManager.class, LeaseManagerLite.class));
			factory.setProxyTargetClass(true);
			Field field = ReflectionUtils.findField(PeerAwareInstanceRegistry.class,
					"instance");
			try {
				// Awful ugly hack to work around lack of DI in eureka
				field.setAccessible(true);
				Field modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				ReflectionUtils.setField(field, null, factory.getProxy());
			}
			catch (Exception e) {
				throw new IllegalStateException("Cannot modify instance registry", e);
			}
		}

	}
	
	@Bean
	public SimpleServiceInstanceRepository serviceInstanceRepository() {
		return new SimpleServiceInstanceRepository();
	}

	@Bean
	public SimpleServiceInstanceBindingRepository serviceInstanceBindingRepository() {
		return new SimpleServiceInstanceBindingRepository();
	}

	@Bean
	public Catalog catalog() {
		return new Catalog(new ArrayList<ServiceDefinition>());
	}
	
	@Bean
	public InstanceInfo instanceInfo(EurekaInstanceConfig config) {
		return new EurekaConfigBasedInstanceInfoProvider(config).get();
	}

	@Bean
	public FilterRegistrationBean jersey() {
		FilterRegistrationBean bean = new FilterRegistrationBean();
		bean.setFilter(new ServletContainer());
		bean.addInitParameter("com.sun.jersey.config.property.WebPageContentRegex",
				"/(flex/|images/|js/|css/|jsp/|admin/|v2/catalog|v2/service_instances).*");
		bean.addInitParameter("com.sun.jersey.config.property.packages",
				"com.sun.jersey;com.netflix");
		return bean;
	}

}
