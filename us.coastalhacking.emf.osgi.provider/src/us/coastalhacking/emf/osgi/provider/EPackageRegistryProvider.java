package us.coastalhacking.emf.osgi.provider;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.log.LogService;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;
import us.coastalhacking.emf.osgi.api.EmfOsgiApi.Property;

@Component(configurationPid = EmfOsgiApi.EPackage_Registry.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = EPackage.Registry.class)
public class EPackageRegistryProvider extends ConcurrentHashMap<String, Object> implements EPackage.Registry {

	private static final long serialVersionUID = -5942228130618962255L;

	protected EPackage.Registry delegateRegistry;

	public EPackageRegistryProvider() {
		super();
		delegateRegistry = EPackage.Registry.INSTANCE;
	}

	@Reference
	LogService logger;

	@Reference(name = EmfOsgiApi.EPackage_Registry.Reference.EPACKAGES, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	void setEPackage(EPackage ePackage, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> put(nsUri, ePackage));
	}

	void unsetEPackage(EPackage ePackage, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> remove(nsUri, ePackage));
	}

	@Reference(name = EmfOsgiApi.EPackage_Registry.Reference.EPACKAGE_DESCRIPTORS, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	void setEPackageDescriptor(EPackage.Descriptor descriptor, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> put(nsUri, descriptor));
	}

	void unsetEPackageDescriptor(EPackage.Descriptor descriptor, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> remove(nsUri, descriptor));
	}

	Optional<String> getUri(Map<String, Object> properties) {
		String nsUri = (String) properties.get(Property.NS_URI);
		Optional<String> result;
		if (nsUri == null || nsUri.isEmpty()) {
			result = Optional.empty();
		} else {
			result = Optional.of(nsUri);
		}
		return result;
	}


	@Override
	public Object put(String key, Object value) {
		if (value instanceof EPackage || value instanceof EPackage.Descriptor) {
			return super.put(key, value);
		}
		final String message = String.format("Object '%s' is not of type EPackage or EPackage.Descriptor via NS URI %s",
				value, key);
		final RuntimeException e = new IllegalArgumentException(message);
		logger.log(LogService.LOG_WARNING, message, e);
		throw e;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		m.entrySet().stream().forEach(es -> put(es.getKey(), es.getValue()));
	}

	private String servicePid = EmfOsgiApi.EPackage_Registry.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		this.servicePid = (String) properties.get(Constants.SERVICE_PID);
	}

	@Override
	public EPackage getEPackage(String nsURI) {
		final Object thing = get(nsURI);
		if (thing instanceof EPackage) {
			return (EPackage) thing;
		} else if (thing instanceof EPackage.Descriptor) {
			return ((EPackage.Descriptor) thing).getEPackage();

		}
		return delegateRegistry.getEPackage(nsURI);

	}

	@Override
	public EFactory getEFactory(String nsURI) {
		final Object thing = get(nsURI);
		if (thing instanceof EPackage) {
			return ((EPackage) thing).getEFactoryInstance();
		} else if (thing instanceof EPackage.Descriptor) {
			return ((EPackage.Descriptor) thing).getEFactory();
		}
		return delegateRegistry.getEFactory(nsURI);
	}

}
