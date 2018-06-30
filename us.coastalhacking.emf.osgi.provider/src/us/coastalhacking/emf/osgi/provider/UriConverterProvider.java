package us.coastalhacking.emf.osgi.provider;

import java.util.Map;

import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;

@Component(configurationPid = EmfOsgiApi.URIConverter.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = URIConverter.class)
public class UriConverterProvider extends ExtensibleURIConverterImpl {

	private String servicePid = EmfOsgiApi.URIConverter.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		this.servicePid = (String) properties.get(Constants.SERVICE_PID);
	}
}
