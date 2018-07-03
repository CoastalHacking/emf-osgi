package us.coastalhacking.emf.osgi.provider;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
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
import us.coastalhacking.emf.osgi.impl.ConcurrentEPackageRegistryImpl;

@Component(configurationPid = EmfOsgiApi.EPackage_Registry.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = EPackage.Registry.class)
public class EPackageRegistryProvider extends ConcurrentEPackageRegistryImpl {

	private static final long serialVersionUID = 1L;

	@Reference(name = EmfOsgiApi.EPackage_Registry.Reference.EPACKAGES, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setEPackage(EPackage ePackage, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> put(nsUri, ePackage));
	}

	protected void unsetEPackage(EPackage ePackage, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> remove(nsUri, ePackage));
	}

	@Reference(name = EmfOsgiApi.EPackage_Registry.Reference.EPACKAGE_DESCRIPTORS, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setEPackageDescriptor(EPackage.Descriptor descriptor, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> put(nsUri, descriptor));
	}

	protected void unsetEPackageDescriptor(EPackage.Descriptor descriptor, Map<String, Object> properties) {
		getUri(properties).ifPresent(nsUri -> remove(nsUri, descriptor));
	}

	@Reference(name = EmfOsgiApi.EPackage_Registry.Reference.DELEGATE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.OPTIONAL, target = EmfOsgiApi.EPackage_Registry.Reference.DELEGATE_TARGET_DEFAULT)
	@Override
	protected void setDelegateRegistry(Registry delegateRegistry) {
		super.setDelegateRegistry(delegateRegistry);
	}

	protected void unsetDelegateRegistry(Registry delegateRegistry) {
		this.delegateRegistry = null;
	}

	protected Optional<String> getUri(Map<String, Object> properties) {
		String nsUri = (String) properties.get(Property.NS_URI);
		Optional<String> result;
		if (nsUri == null || nsUri.isEmpty()) {
			result = Optional.empty();
		} else {
			result = Optional.of(nsUri);
		}
		return result;
	}

	// Does not need to be volatile since it's set in @Activate
	private String servicePid = EmfOsgiApi.EPackage_Registry.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		Optional.ofNullable((String) properties.get(Constants.SERVICE_PID)).ifPresent(pid -> this.servicePid = pid);
	}

}
