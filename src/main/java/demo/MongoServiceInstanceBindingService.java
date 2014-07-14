package demo;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.community.servicebroker.exception.ServiceBrokerException;
import org.cloudfoundry.community.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;
import org.cloudfoundry.community.servicebroker.model.ServiceInstance;
import org.cloudfoundry.community.servicebroker.model.ServiceInstanceBinding;
import org.cloudfoundry.community.servicebroker.service.ServiceInstanceBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.appinfo.InstanceInfo;

/**
 * Mongo impl to bind services.
 *  
 * @author sgreenberg@gopivotal.com
 *
 */
@Service
public class MongoServiceInstanceBindingService implements ServiceInstanceBindingService {

	private MongoServiceInstanceBindingRepository repository;
	private Catalog catalog;
	
	@Autowired
	public MongoServiceInstanceBindingService(Catalog catalog, MongoServiceInstanceBindingRepository repository) {
		this.catalog = catalog;
		this.repository = repository;
	}
	
	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(
			String bindingId, ServiceInstance serviceInstance,
			String serviceId, String planId, String appGuid)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		
		ServiceInstanceBinding binding = repository.findOne(bindingId);
		if (binding != null) {
			throw new ServiceInstanceBindingExistsException(binding);
		}
		
		Map<String,Object> credentials = new HashMap<String,Object>();
		
		credentials.put("uri", findUriFromService(serviceId));
		
		binding = new ServiceInstanceBinding(bindingId, serviceInstance.getId(), credentials, null, appGuid);
		repository.save(binding);
		
		return binding;
	}

	protected String findUriFromService(String serviceId) {
		for (ServiceDefinition definition : catalog.getServiceDefinitions()) {
			if (definition.getId().equals(serviceId)) {
				InstanceInfo info = (InstanceInfo) definition.getMetadata().get("info");
				return info.getHomePageUrl();
			}
		}
		throw new IllegalStateException("Cannot locate service in catalog: " + serviceId);
	}

	@Override
	public ServiceInstanceBinding getServiceInstanceBinding(String id) {
		return repository.findOne(id);
	}

	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(String id) throws ServiceBrokerException {
		ServiceInstanceBinding binding = getServiceInstanceBinding(id);
		if (binding!= null) { 
			repository.delete(id);
		}
		return binding;
	}

}
