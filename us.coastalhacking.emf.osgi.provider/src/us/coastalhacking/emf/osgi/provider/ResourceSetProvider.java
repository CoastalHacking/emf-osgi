package us.coastalhacking.emf.osgi.provider;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;

@Component(configurationPid = EmfOsgiApi.ResourceSet.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = ResourceSet.class)
public class ResourceSetProvider extends ResourceSetImpl {

	// protected super member 'converter' isn't marked as volatile, mask it
	private volatile URIConverter converter;
	private volatile EPackage.Registry ePackageRegistry;
	private volatile Factory.Registry factoryRegistry;

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.URI_CONVERTER, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setURIConverter(URIConverter uriConverter) {
		this.converter = uriConverter;
	}

	void unsetURIConverter(URIConverter converter) {
		// Do not delegate to global instance
		this.converter = null;
	}

	@Override
	public URIConverter getURIConverter() {
		return this.converter;
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.EPACKAGE_REGISTRY, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = packageRegistry;
	}

	void unsetPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = null;
	}

	@Override
	public Registry getPackageRegistry() {
		return ePackageRegistry;
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.RESOURCE_FACTORY_REGISTRY, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	void unsetResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = null;
	}

	@Override
	public Factory.Registry getResourceFactoryRegistry() {
		return factoryRegistry;
	}

	private String servicePid = EmfOsgiApi.ResourceSet.PID;

	@Activate
	void activate(Map<String, Object> properties) {
		this.servicePid = (String) properties.get(Constants.SERVICE_PID);
	}

	@Override
	public String toString() {
		return servicePid;
	}

}