package us.coastalhacking.emf.osgi.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;

public class ConcurrentResourceFactoryRegistryImpl extends ResourceFactoryRegistryImpl {

	protected volatile URIConverter uriConverter;

	public ConcurrentResourceFactoryRegistryImpl() {
		super();
		// Override protected hash maps with concurrent versions in ctor
		// Otherwise, need to mask, redeclare with volatile, and override more super
		// methods
		this.protocolToFactoryMap = new ConcurrentHashMap<>();
		this.contentTypeIdentifierToFactoryMap = new ConcurrentHashMap<>();
		this.extensionToFactoryMap = new ConcurrentHashMap<>();
	}

	protected void setUriConverter(URIConverter uriConverter) {
		this.uriConverter = uriConverter;
	}

	@Override
	protected URIConverter getURIConverter() {
		return uriConverter;
	}

}
