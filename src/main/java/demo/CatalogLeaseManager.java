/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.cloudfoundry.community.servicebroker.model.Catalog;
import org.cloudfoundry.community.servicebroker.model.Plan;
import org.cloudfoundry.community.servicebroker.model.ServiceDefinition;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.InstanceInfo.ActionType;
import com.netflix.eureka.lease.LeaseManager;

/**
 * @author Dave Syer
 *
 */
public class CatalogLeaseManager implements LeaseManager<InstanceInfo>, LeaseManagerLite {

	private Catalog catalog;

	public CatalogLeaseManager(Catalog catalog, InstanceInfo config) {
		this.catalog = catalog;
		register(config, false);
	}

	@Override
	public void register(InstanceInfo info, boolean isReplication) {
		register(info, 0, isReplication);
	}

	@Override
	public void register(InstanceInfo info, int leaseDuration, boolean isReplication) {
		List<ServiceDefinition> definitions = catalog.getServiceDefinitions();
		for (ServiceDefinition definition : new ArrayList<ServiceDefinition>(definitions)) {
			if (definition.getName().equals(info.getAppName())) {
				return;
			}
		}
		ServiceDefinition definition = getServiceDefinition(info);
		definitions.add(definition);
	}

	private ServiceDefinition getServiceDefinition(InstanceInfo info) {
		ServiceDefinition definition = new ServiceDefinition(
				UUID.randomUUID().toString(), info.getAppName(),
				"Eureka-brokered service", true, getPlans(info.getAppName()));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("info", (Object) info);
		map.put("timestamp", System.currentTimeMillis());
		definition.setMetadata(map);
		return definition;
	}

	private List<Plan> getPlans(String appName) {
		Plan plan = new Plan(appName + "-plan", "Default Service Plan",
				"This is a default service plan.  All services are created equally.",
				getServiceDefinitionMetadata(appName), true);
		return Arrays.asList(plan);
	}

	@Override
	public boolean cancel(String appName, String id, boolean isReplication) {
		List<ServiceDefinition> definitions = catalog.getServiceDefinitions();
		for (ServiceDefinition definition : new ArrayList<ServiceDefinition>(definitions)) {
			if (definition.getName().equals(appName)) {
				definitions.remove(definition);
				break;
			}
		}
		return true;
	}

	@Override
	public boolean renew(String appName, String id, boolean isReplication) {
		List<ServiceDefinition> definitions = catalog.getServiceDefinitions();
		for (ServiceDefinition definition : new ArrayList<ServiceDefinition>(definitions)) {
			if (definition.getName().equals(appName)) {
				definition.getMetadata().put("timestamp", System.currentTimeMillis());
				break;
			}
		}
		return true;
	}

	@Override
	public void evict() {
		List<ServiceDefinition> definitions = catalog.getServiceDefinitions();
		for (ServiceDefinition definition : new ArrayList<ServiceDefinition>(definitions)) {
			InstanceInfo info = (InstanceInfo) definition.getMetadata().get("info");
			if (info.getActionType() == ActionType.DELETED) {
				definitions.remove(definition);
			}
		}
	}

	private Map<String, Object> getServiceDefinitionMetadata(String appName) {
		Map<String, Object> sdMetadata = new HashMap<String, Object>();
		sdMetadata.put("displayName", appName + "-service");
		sdMetadata.put("longDescription", "Platform Service for " + appName);
		sdMetadata.put("providerDisplayName", "Pivotal");
		sdMetadata.put("documentationUrl", "https://github.com/spring-platform");
		sdMetadata.put("supportUrl", "https://github.com/spring-platform");
		return sdMetadata;
	}

}
