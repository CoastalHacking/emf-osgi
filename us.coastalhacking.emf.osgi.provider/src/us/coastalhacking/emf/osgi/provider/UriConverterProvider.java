package us.coastalhacking.emf.osgi.provider;

import java.util.Map;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import us.coastalhacking.emf.osgi.api.EmfOsgiApi;
import us.coastalhacking.emf.osgi.impl.ConcurrentUriConverterImpl;

@Component(configurationPid = EmfOsgiApi.URIConverter.PID, configurationPolicy = ConfigurationPolicy.OPTIONAL, service = URIConverter.class)
public class UriConverterProvider extends ConcurrentUriConverterImpl {

	@Reference(name= EmfOsgiApi.URIConverter.Reference.URI_HANDLERS, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setUriHandler(URIHandler uriHandler) {
		getURIHandlers().add(uriHandler);
	}
	
	protected void unsetUriHandler(URIHandler uriHandler) {
		getURIHandlers().remove(uriHandler);
	}
	
	@Reference(name= EmfOsgiApi.URIConverter.Reference.CONTENT_HANDLERS, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.MULTIPLE)
	protected void setContentHandler(ContentHandler contentHandler) {
		getContentHandlers().add(contentHandler);
	}
	
	protected void unsetContentHandler(ContentHandler contentHandler) {
		getContentHandlers().remove(contentHandler);
	}
	
	private String servicePid = EmfOsgiApi.URIConverter.PID;

	@Override
	public String toString() {
		return servicePid;
	}

	@Activate
	void activate(Map<String, Object> properties) {
		Optional.ofNullable((String) properties.get(Constants.SERVICE_PID)).ifPresent(pid -> this.servicePid = pid);
	}
}
