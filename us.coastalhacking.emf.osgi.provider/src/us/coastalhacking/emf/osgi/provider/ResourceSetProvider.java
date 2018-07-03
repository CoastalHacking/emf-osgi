package us.coastalhacking.emf.osgi.provider;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.DelegatingEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
import us.coastalhacking.emf.osgi.impl.ConcurrentResourceSetImpl;

@Component(configurationPid = EmfOsgiApi.ResourceSet.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = ResourceSet.class)
public class ResourceSetProvider extends ConcurrentResourceSetImpl {

	// mask, declared as volatile
	protected volatile URIConverter converter;
	protected volatile EPackage.Registry ePackageRegistry;
	protected volatile Factory.Registry factoryRegistry;

	protected final EList<AdapterFactory> delegatingFactories = new DelegatingEList<AdapterFactory>() {

		private static final long serialVersionUID = 1L;
		private final List<AdapterFactory> adapterFactories = new CopyOnWriteArrayList<>();

		@Override
		protected List<AdapterFactory> delegateList() {
			return adapterFactories;
		}
	};

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.URI_CONVERTER, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setURIConverter(URIConverter uriConverter) {
		super.setURIConverter(uriConverter);
	}

	protected void unsetURIConverter(URIConverter converter) {
		// Do not delegate to global instance
		// This varies from ResourceSetImpl since it's now nullable
		this.converter = null;
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.EPACKAGE_REGISTRY, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setPackageRegistry(EPackage.Registry packageRegistry) {
		super.setPackageRegistry(packageRegistry);
	}

	protected void unsetPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = null;
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.RESOURCE_FACTORY_REGISTRY, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		super.setResourceFactoryRegistry(factoryRegistry);
	}

	protected void unsetResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = null;
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.ADAPTER_FACTORY, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setAdapterFactory(AdapterFactory adapterFactory) {
		getAdapterFactories().add(adapterFactory);
	}

	protected void unsetAdapterFactory(AdapterFactory adapterFactory) {
		getAdapterFactories().remove(adapterFactory);
	}

	@Reference(name = EmfOsgiApi.ResourceSet.Reference.RESOURCE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setResource(Resource resource) {
		getResources().add(resource);
	}

	protected void unsetResource(Resource resource) {
		getResources().remove(resource);
	}

	private String servicePid = EmfOsgiApi.ResourceSet.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		Optional.ofNullable((String) properties.get(Constants.SERVICE_PID)).ifPresent(pid -> this.servicePid = pid);
	}
}