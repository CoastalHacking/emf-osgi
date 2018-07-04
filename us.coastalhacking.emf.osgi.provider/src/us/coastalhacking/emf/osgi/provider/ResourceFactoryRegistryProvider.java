package us.coastalhacking.emf.osgi.provider;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;
import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;
import us.coastalhacking.emf.osgi.impl.ConcurrentResourceFactoryRegistryImpl;

@Component(configurationPid = EmfOsgiApi.Resource_Factory_Registry.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = Factory.Registry.class)
public class ResourceFactoryRegistryProvider extends ConcurrentResourceFactoryRegistryImpl {

	@Reference(name = EmfOsgiApi.Resource_Factory_Registry.Reference.URI_CONVERTER, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	protected void setUriConverter(URIConverter uriConverter) {
		super.setUriConverter(uriConverter);
	}

	protected void unsetUriConverter(URIConverter uriConverter) {
		super.setUriConverter(null);
	}

	private void putIntoFactoryMap(Map<String, Object> properties, String factoryType, Map<String, Object> factoryMap,
			Object factoryOrDescriptor) {
		putIntoOrRemoveFromFactoryMap(true, properties, factoryType, factoryMap, factoryOrDescriptor);
	}

	private void removeFromFactoryMap(Map<String, Object> properties, String factoryType,
			Map<String, Object> factoryMap, Object factoryOrDescriptor) {
		putIntoOrRemoveFromFactoryMap(false, properties, factoryType, factoryMap, factoryOrDescriptor);
	}

	private void putIntoOrRemoveFromFactoryMap(boolean put, Map<String, Object> properties, String factoryType,
			Map<String, Object> factoryMap, Object factoryOrDescriptor) {
		Optional.ofNullable((String) properties.get(factoryType)).ifPresent(key -> {
			if (put)
				factoryMap.put(key, factoryOrDescriptor);
			else
				factoryMap.remove(key, factoryOrDescriptor);
		});
	}

	@Reference(name = EmfOsgiApi.Resource_Factory_Registry.Reference.RESOURCE_FACTORIES, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	void setFactory(Factory factory, Map<String, Object> properties) {
		putIntoFactoryMap(properties, Property.SCHEME, protocolToFactoryMap, factory);
		putIntoFactoryMap(properties, Property.EXTENSION, extensionToFactoryMap, factory);
		putIntoFactoryMap(properties, Property.CONTENT_TYPE, contentTypeIdentifierToFactoryMap, factory);
	}

	void unsetFactory(Factory factory, Map<String, Object> properties) {
		removeFromFactoryMap(properties, Property.SCHEME, protocolToFactoryMap, factory);
		removeFromFactoryMap(properties, Property.EXTENSION, extensionToFactoryMap, factory);
		removeFromFactoryMap(properties, Property.CONTENT_TYPE, contentTypeIdentifierToFactoryMap, factory);
	}

	@Reference(name = EmfOsgiApi.Resource_Factory_Registry.Reference.RESOURCE_FACTORY_DESCRIPTORS, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	void setDescriptor(Factory.Descriptor descriptor, Map<String, Object> properties) {
		putIntoFactoryMap(properties, Property.SCHEME, protocolToFactoryMap, descriptor);
		putIntoFactoryMap(properties, Property.EXTENSION, extensionToFactoryMap, descriptor);
		putIntoFactoryMap(properties, Property.CONTENT_TYPE, contentTypeIdentifierToFactoryMap, descriptor);

	}

	void unsetDescriptor(Factory.Descriptor descriptor, Map<String, Object> properties) {
		removeFromFactoryMap(properties, Property.SCHEME, protocolToFactoryMap, descriptor);
		removeFromFactoryMap(properties, Property.EXTENSION, extensionToFactoryMap, descriptor);
		removeFromFactoryMap(properties, Property.CONTENT_TYPE, contentTypeIdentifierToFactoryMap, descriptor);
	}

	private String servicePid = EmfOsgiApi.Resource_Factory_Registry.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		Optional.ofNullable((String) properties.get(Constants.SERVICE_PID)).ifPresent(pid -> this.servicePid = pid);
	}

}
